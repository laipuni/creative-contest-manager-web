import React, {useEffect, useState} from 'react'
import "./teamList.css"
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import "../../styles/pagination.css"
import apiClient from "../../templates/apiClient";
import {all} from "axios";


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

const TeamList = () => {
    const [testYear, setTestYear] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const [testData, setTestData] = useState([]);
    const [selectedTeams, setSelectedTeams] = useState({});
    const [level, setLevel] = useState('초/중등');
    const [contests, setContests] = useState([]);
    const [contestId, setContestId] = useState(null);
    const [lastPage, setLastPage] = useState(0);

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
                        setLastPage(lastPage);
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
    }, [])

    useEffect(() => {
        if (contests.length > 0 && testYear === 0) {
            setTestYear(contests[0].season); // 최신 회차 자동 선택
        }
    }, [contests]);

    //수준별, 회차별 데이터 변경
    useEffect(() => {
        const matched = contests.find((c) => c.season === Number(testYear));
        if (!matched) return;

        const { contestId } = matched;
        setContestId(contestId);  // 상태 업데이트

        // 팀 목록 API 호출
        apiClient.get(`/api/admin/contests/${contestId}/teams`, {
            params: {
                page: 0,  // 첫 페이지
            },
        }).then((res) => {
            const data = res.data.data;
            setTestData(data.teamList);   // 팀 리스트
            setLastPage(data.lastPage);   // 전체 페이지 수
            setCurrentPage(1);            // 현재 페이지 초기화
        }).catch((err) => {
        });
    }, [testYear, level]);


    //데이터 변경되거나 페이지 이동
    useEffect(() => {
        if (contestId === null) return;

        apiClient.get(`/api/admin/contests/${contestId}/teams`, {
            params: {
                page: currentPage - 1,  // 0부터 시작
            }
        }).then((res) => {
            const data = res.data.data;
            console.log(data);
            setTestData(data.teamList);
        }).catch((err) => {
        });
    }, [currentPage]);

    /*------------해당 대회 답안 일괄 다운로드-----------*/
    function handleDownload() {
        const matched = contests.find((c) => c.contestId === contestId);
        if (!matched) return;

        const { season } = matched;

        apiClient.get(`/api/admin/v1/contests/${contestId}/answers/zip-download?`, {
            responseType: 'blob'
        })
            .then((res) => {
                const blob = new Blob([res.data], { type: 'application/zip' });
                const url = window.URL.createObjectURL(blob);

                const link = document.createElement('a');
                link.href = url;
                link.download = `${season}회차_답안.zip`; // 원하는 파일명
                document.body.appendChild(link);
                link.click();

                // 정리
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
            })
            .catch((err) => {
            });
    }


    /*------------합격자 선정------------------*/
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
                               onClick={handleDownload}>답안 일괄 다운로드 📄</p>
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
                                <p className="admin-teamList-body-title-text">팀장 id</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text">합격자</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text">합격 여부</p>
                            </div>
                        </div>
                        {testData.map(team => (
                            <div key={team.id} className="admin-teamList-body-title">
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{team.name}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{team.leaderId}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    {team.winner && <p className="admin-teamList-body-title-text">O</p>}
                                    {!team.winner && <p className="admin-teamList-body-title-text">X</p>}
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <input
                                        className="admin-teamList-body-title-text"
                                        type="checkbox"
                                        checked={team.winner === true}
                                        onChange={() => toggleTeamSelection(team.teamId)}
                                    />
                                </div>
                            </div>
                        ))}

                    </div>
                    <div className="pastTest-pagination-container">
                        <div className="pastTest-pagination">
                            {lastPage !== 0 && <Pagination
                                totalPages={lastPage}
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