import React, {useEffect, useState} from 'react'
import "./teamList.css"
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import "../../styles/pagination.css"
import apiClient from "../../templates/apiClient";
import {all} from "axios";

const ITEMS_PER_PAGE = 10;
const LatestYear = 12;
// 예시 데이터
const exampleData = Array.from({ length: 23 }, (_, i) => ({
    id: i + 1,
    teamName: `${i + 1}팀`,
    memberCnt: 3,
    fileLink: `/downloads/file_${i + 1}.pdf`,
    score: -1,
    date: `2025-04-${String((i % 30) + 1).padStart(2, '0')}`
}));

function Pagination({ totalPages, currentPage, onPageChange }) {
    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    const isDisabled = totalPages === 0;

    return (
        <div className="pagination">
            <button disabled={isDisabled || currentPage === 1} onClick={() => onPageChange(1)}>«</button>
            <button disabled={isDisabled || currentPage === 1} onClick={() => onPageChange(currentPage - 1)}>‹</button>

            {pages.map(page => (
                <button
                    key={page}
                    className={page === currentPage ? 'active' : ''}
                    onClick={() => onPageChange(page)}
                >
                    {page}
                </button>
            ))}

            <button disabled={isDisabled || currentPage === totalPages} onClick={() => onPageChange(currentPage + 1)}>›</button>
            <button disabled={isDisabled || currentPage === totalPages} onClick={() => onPageChange(totalPages)}>»</button>
        </div>
    );
}

function handleDownload() {
    //TODO- 다운로드 api 연동

}
const TeamList = () => {
    const [testYear, setTestYear] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const [testData, setTestData] = useState([]);
    const [currentData, setCurrentData] = useState([]);
    const [selectedTeams, setSelectedTeams] = useState({});
    const [level, setLevel] = useState('초/중등');
    const [contests, setContests] = useState([]);

    //회차 정보 받아오기
    useEffect(() => {
        let page = 0;
        let lastPage = 0;
        let allContests = [];

        const fetchContests = () => {
            apiClient.get(`/api/admin/contests?page=${page}`)
                .then((res) => {
                    const data = res.data.data;
                    lastPage = data.lastPage;
                    const mapped = data.problemList.map((contest) => ({
                        season: contest.season,
                        contestId: contest.contestId
                    }));
                    allContests = [...allContests, ...mapped];
                    page++;

                    if (page <= lastPage) {
                        fetchContests();
                    } else {
                        // 시즌 내림차순 정렬
                        allContests.sort((a, b) => b.season - a.season);
                        setContests(allContests);
                    }
                })
                .catch((err) => {
                });
        };

        fetchContests();
        console.log(allContests)
    }, [])

    //수준별, 회차별 데이터 변경
    useEffect(() => {
        let filtered = [];
        /*if (level === '초/중등') {
            filtered = exampleData;
        }*/
        setTestData(filtered);
        setCurrentPage(1);
    }, [level, testYear]);

    //데이터 변경되거나 페이지 이동
    useEffect(() => {
        const startIdx = (currentPage - 1) * ITEMS_PER_PAGE;
        setCurrentData(testData.slice(startIdx, startIdx + ITEMS_PER_PAGE));
    }, [testData, currentPage]);

    const toggleTeamSelection = (teamId) => {
        setSelectedTeams(prev => ({
            ...prev,
            [teamId]: !prev[teamId]
        }));
    };

    const handleBulkPass = () => {
        const passedTeamIds = Object.keys(selectedTeams).filter(id => selectedTeams[id]);
        console.log("합격 처리할 팀 ID:", passedTeamIds);

        // 여기에서 API 호출 등 합격 처리 로직 수행
    };

    const totalPages = Math.ceil(testData.length / ITEMS_PER_PAGE);


    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox">
                            <div className="admin-teamList-title">팀 목록</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                        <div className="admin-teamList-selectbox">
                            <select
                                value={level}
                                onChange={(e) => setLevel(e.target.value)}
                                required>
                                <option value="초/중등">초/중등부</option>
                                <option value="고등/일반">고등/일반부</option>
                            </select>
                            <select
                                value={testYear}
                                onChange={(e) => setTestYear(e.target.value)}
                                required>
                                {contests.map((c) => (
                                    <option key={c.season} value={c.season}>
                                        {c.season}회차
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>
                    <div className="admin-teamList-body">
                        <div style={{flexDirection: 'row', width: '100%', display: 'flex'}}>
                            <p className="admin-teamList-download"
                               onClick={handleDownload}>문제 일괄 다운로드 📄</p>
                            <button
                                className="admin-pass-button"
                                onClick={() => handleBulkPass()}
                            >
                                합격자 선정
                            </button>
                        </div>
                        <div className="admin-teamList-body-title">
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text">팀 이름</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text">인원</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text">합격 여부</p>
                            </div>
                        </div>
                        {currentData.map(team => (
                            <div key={team.id} className="admin-teamList-body-title">
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{team.teamName}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{team.memberCnt}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <input
                                        className="admin-teamList-body-title-text"
                                        type="checkbox"
                                        checked={!!selectedTeams[team.id]}
                                        onChange={() => toggleTeamSelection(team.id)}
                                    />
                                </div>
                            </div>
                        ))}

                    </div>
                    <div className="pastTest-pagination-container">
                        <div className="pastTest-pagination">
                            {totalPages !== 0 && <Pagination
                                totalPages={totalPages}
                                currentPage={currentPage}
                                onPageChange={setCurrentPage}
                            />}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TeamList;