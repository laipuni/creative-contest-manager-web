import React, { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import './testManage.css';
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import planImage from "../../styles/images/admin_register_day.png";
import testImage from "../../styles/images/admin_register_problem.png";
import apiClient from "../../templates/apiClient";
import axios from "axios";

const TestManage = () => {
    // --- 일정 관련 상태 ---
    const [latestContest, setLatestContest] = useState({ season: 0, contestId: null });
    const [registerStartDate, setRegisterStartDate] = useState('');
    const [registerEndDate, setRegisterEndDate] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [isDateModalOpen, setIsDateModalOpen] = useState(false);
    const [modalTab, setModalTab] = useState('접수');
    const [tempRegisterStartDate, setTempRegisterStartDate] = useState(null);
    const [tempRegisterEndDate, setTempRegisterEndDate] = useState(null);
    const [tempStartDate, setTempStartDate] = useState(null);
    const [tempEndDate, setTempEndDate] = useState(null);
    const [isEditMode, setIsEditMode] = useState(false);
    const [isRegistered, setIsRegistered] = useState(false);

    // --- 문제 등록 관련 상태 ---
    const [commonQuiz, setCommonQuiz] = useState([]);         // 등록된 COMMON 문제
    const [easyQuiz, setEasyQuiz] = useState([]);             // 등록된 ELEMENTARY_MIDDLE 문제
    const [hardQuiz, setHardQuiz] = useState([]);             // 등록된 HIGH_NORMAL 문제
    const [commonTempFile, setCommonTempFile] = useState(null);
    const [easyTempFile, setEasyTempFile] = useState(null);
    const [hardTempFile, setHardTempFile] = useState(null);
    const [checkedTypes, setCheckedTypes] = useState({
        '공통': false,
        '초/중등': false,
        '고등/일반': false
    });

    //최초 랜더링 시 마지막 대회 정보 들고오기
    useEffect(() => {
        apiClient.get('/api/admin/contests/latest')
            .then((res) => {
                if(res.data.data){
                   setLatestContest({
                       season: res.data.data.season,
                       contestId: res.data.data.contestId});
                    setRegisterStartDate(formatDate(new Date(res.data.data.registrationStartAt)));
                    setRegisterEndDate(formatDate(new Date(res.data.data.registrationEndAt)));
                    setStartDate(formatDate(new Date(res.data.data.startTime)));
                    setEndDate(formatDate(new Date(res.data.data.endTime)));
                }
                else{
                    setLatestContest({
                        season: 0,
                        contestId: null
                    })
                    setRegisterStartDate(null);
                    setRegisterEndDate(null);
                    setStartDate(null);
                    setEndDate(null);
                }
            })
            .catch((err)=>{})
    }, [isRegistered])

    //최초 랜더링 or 대회 삭제됐을 때 문제 갱신
    useEffect(()=>{
        if(!latestContest.contestId)
            return;
        apiClient.get(`/api/admin/v1/contests/${latestContest.contestId}/problems`, {
            parameter: {page : 0}, skipErrorHandler: true})
            .then((res) => {
                const problemList = res.data.data.problemList;
                setCommonQuiz(problemList.filter(problem => problem.section === 'COMMON'));
                setHardQuiz(problemList.filter(problem => problem.section === 'HIGH_NORMAL'));
                setEasyQuiz(problemList.filter(problem => problem.section === 'ELEMENTARY_MIDDLE'));
                // 새 첨부 파일은 초기화
                setCommonTempFile(null);
                setEasyTempFile(null);
                setHardTempFile(null);
                // 체크박스 초기화
                setCheckedTypes({ '공통': false, '초/중등': false, '고등/일반': false });
            })
            .catch((err)=>{})
    }, [latestContest.contestId])


    //일정 등록
    const handleRegisterDate = () => {
        if (!tempRegisterStartDate || !tempRegisterEndDate || !tempStartDate || !tempEndDate) {
            alert('접수 기간과 대회 기간의 시작일과 종료일을 모두 선택해주세요.');
            return;
        }

        if ((tempRegisterStartDate > tempRegisterEndDate) || (tempStartDate > tempEndDate)) {
            alert('종료일이 시작일보다 빠르게 설정되었습니다.');
            return;
        }

        const contestTitle = `${latestContest.season+1}회차 cps 경진대회`;
        apiClient.post('/api/admin/contests', {
            title: contestTitle, season: latestContest.season+1,
            registrationStartAt: toISOStringWithUTC9(tempRegisterStartDate), registrationEndAt: toISOStringWithUTC9(tempRegisterEndDate),
            contestStartAt: toISOStringWithUTC9(tempStartDate), contestEndAt: toISOStringWithUTC9(tempEndDate)
        }, {skipErrorHandler: true})
            .then((res) => {
                setRegisterStartDate(formatDate(tempRegisterStartDate));
                setRegisterEndDate(formatDate(tempRegisterEndDate));
                setStartDate(formatDate(tempStartDate));
                setEndDate(formatDate(tempEndDate));
                setIsDateModalOpen(false);
                setIsRegistered(!isRegistered);
            })
            .catch((err)=>{
                alert(err.response.data.message)})
    };

    //일정 수정
    const openEditModal = () => {
        if(latestContest.season <= 0){
            alert('등록을 먼저 해주세요');
            return;
        }
        setTempRegisterStartDate(registerStartDate);
        setTempRegisterEndDate(registerEndDate);
        setTempStartDate(startDate);
        setTempEndDate(endDate);
        setIsEditMode(true);
        setIsDateModalOpen(true);
    };

    const handleConfirm = () => {
        if (isEditMode) {
            // 문자열 또는 Date 모두 처리
            const parseDate = (d) => typeof d === 'string' ? new Date(d.replace(/\./g, '-')) : d;

            const rStart = parseDate(tempRegisterStartDate);
            const rEnd = parseDate(tempRegisterEndDate);
            const cStart = parseDate(tempStartDate);
            const cEnd = parseDate(tempEndDate);

            if ((rStart > rEnd) || (cStart > cEnd)) {
                alert('종료일이 시작일보다 빠르게 설정되었습니다.');
                return;
            }

            const contestTitle = `${latestContest.season}회차 cps 경진대회`;
            apiClient.put('/api/admin/contests', {
                title: contestTitle, season: latestContest.season, contestId: latestContest.contestId,
                registrationStartAt: toISOStringWithUTC9(tempRegisterStartDate), registrationEndAt: toISOStringWithUTC9(tempRegisterEndDate),
                contestStartAt: toISOStringWithUTC9(tempStartDate), contestEndAt: toISOStringWithUTC9(tempEndDate)
            }, {skipErrorHandler: true})
                .then((res) => {
                    setIsDateModalOpen(false);
                    setIsEditMode(false);
                    setIsRegistered(!isRegistered);
                })
                .catch((err)=>{
                    alert(err.response.data.message)})
        } else {
            handleRegisterDate();
        }

    };

    const handleCloseModal = () => {
        // 수정 모드일 경우 원래 값 유지
        if (isEditMode) {
            setTempRegisterStartDate(registerStartDate);
            setTempRegisterEndDate(registerEndDate);
            setTempStartDate(startDate);
            setTempEndDate(endDate);
        }
        setIsDateModalOpen(false);
    };


    // 날짜 포맷을 'yyyy.MM.dd'로 변환
    const formatDate = (date) => {
        if (typeof date === 'string') {
            // 이미 포맷처리 된 경우
            return date;
        }
        if (date instanceof Date && !isNaN(date)) {
            const y = date.getFullYear();
            const m = String(date.getMonth() + 1).padStart(2, '0');
            const d = String(date.getDate()).padStart(2, '0');
            return `${y}.${m}.${d}`;
        }
    };

    // 날짜를 UTC+9 형태로 변환
    const toISOStringWithUTC9 = (value) => {
        // 이미 문자열인 경우 (ex. "2025.05.15")
        if (typeof value === 'string') {
            // 문자열 -> Date로 파싱 (format: yyyy.MM.dd 기준)
            const [year, month, day] = value.split('.').map(Number);
            if (!year || !month || !day) return value; // 파싱 실패 시 그대로 반환

            const localDate = new Date(year, month - 1, day);
            // UTC 기준으로 9시간 빼서 보정
            const utcDate = new Date(localDate.getTime() + 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        // Date 객체인 경우
        if (value instanceof Date) {
            const utcDate = new Date(value.getTime() - 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        // 다른 타입이면 그대로 반환
        return value;
    };




    //일정 삭제
    const handleDeleteDate = () => {
        apiClient.delete('/api/admin/contests', {
            data: { contestId: latestContest.contestId }, skipErrorHandler: true})
            .then((res)=>{
                setIsRegistered(!isRegistered);
            })
            .catch((err)=>{})
    }

    //문제 체크박스 선택
    const toggleTypeCheck = (type) => {
        setCheckedTypes(prev => ({
            ...prev,
            [type]: !prev[type]
        }));
    };

    // 문제 등록 or 수정
    const handleRegisterSelectedTypes = async (e) => {
        e.preventDefault();
        console.log(easyQuiz[0]);
        const sectionMap = {
            '공통': { file: commonTempFile, section: 'COMMON', existing: commonQuiz[0]},
            '초/중등': { file: easyTempFile, section: 'ELEMENTARY_MIDDLE', existing: easyQuiz[0]},
            '고등/일반': { file: hardTempFile, section: 'HIGH_NORMAL', existing: hardQuiz[0]},
        };

        const typesToUpload = Object.keys(checkedTypes).filter(type => checkedTypes[type]);

        if (typesToUpload.length === 0) {
            alert('섹션을 체크해주세요.');
            return;
        }

        const promises = [];

        for (const type of typesToUpload) {
            const { file, section, existing } = sectionMap[type] || {};
            if (!file) {
                alert('변경된 파일만 선택해주세요.');
                return;
            }

            const formData = new FormData();

            const jsonBlob = new Blob([JSON.stringify({
                title: file.name,
                section,
                season: latestContest.season,
                contestId: latestContest.contestId,
                problemOrder: 1,
                deleteFileIds: []
            })], { type: 'application/json' });

            formData.append('request', jsonBlob);
            formData.append('multipartFiles', file);

            const method = existing?.problemId?  'put' : 'post';
            const url = existing?.problemId
                ? `/api/admin/contests/${latestContest.contestId}/problems/${existing.problemId}`  // PUT일 경우 ID 포함
                : `/api/admin/problems/real`;


            promises.push(
                axios[method](url, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                })
            );
        }

        try {
            await Promise.all(promises);
            alert('문제가 등록되었습니다.');
            setCheckedTypes({
                '초/중등': false,
                '공통': false,
                '고등/일반': false
            });
            setIsRegistered(r => !r);
        } catch (error) {
            console.error(error);
            alert(error.response.data.message);
        }
    };




    //문제 다운로드
    const handleDownload = async (problemId) => {
        try {
            const res = await apiClient.get(`/api/admin/v1/contests/${latestContest.contestId}/problems/${problemId}`);
            const fileId = res.data.data.fileList[0]?.fileId;
            if (fileId) {
                // 파일 다운로드
                apiClient.get(`/api/admin/files/${fileId}`)
                    .then((res)=>{})
                    .catch((err)=>{})
            } else {
                alert('파일이 존재하지 않습니다.');
            }
        } catch (err) {
        }
    };

    //선택된 문제 삭제하기
    const handleDeleteSelectedTypes = async () => {
        const sectionMap = {
            '공통': commonQuiz,
            '초/중등': easyQuiz,
            '고등/일반': hardQuiz,
        };

        const typesToDelete = Object.keys(checkedTypes).filter(type => checkedTypes[type]);
        if (typesToDelete.length === 0) {
            alert('항목을 선택해주세요');
            return;
        }

        const deletePromises = [];

        typesToDelete.forEach(type => {
            const quiz = sectionMap[type];
            console.log(quiz);
            if (quiz[0]?.problemId) {
                deletePromises.push(axios.delete(`/api/admin/contests/problems`, {
                    data: { deleteProblemId: quiz[0].problemId }
                }))}

            // 상태 초기화
            if (type === '공통') setCommonQuiz([]);
            if (type === '초/중등') setEasyQuiz([]);
            if (type === '고등/일반') setHardQuiz([]);
        });

        try {
            await Promise.all(deletePromises);
            alert('삭제되었습니다.');
        } catch (error) {
            console.error(error);
            alert('삭제 중 오류가 발생했습니다.');
        }
        setIsRegistered(r=> !r);
        setCheckedTypes({
            '초/중등': false,
            '공통': false,
            '고등/일반': false
        });
    };

    //문제 등록 관리
    const handleFileChange = (e, setFile) => {
        const file = e.target.files[0];
        if (!file) return;

        if (file.type !== "application/pdf") {
            alert("PDF 파일만 업로드 가능합니다");
            return;
        }

        setFile(file);
    }

    return (
        <div className="admin-teamList-container">
            <AdminHeader />
            <div className="admin-main-container">
                <AdminSidebar />
                <div className="admin-testManage-main-container">
                    <div className="admin-testManage-detail-container">
                        <div className="admin-testManage-detail-top">
                            <p className="admin-testManage-detail-text">일정 <span style={{ color: 'black' }}>등록</span></p>
                            <div className="admin-testManage-detail-underLine"></div>
                        </div>
                        <img src={planImage} alt="planImage" className="admin-testManage-image" />
                        <div className="admin-testManage-detail-bot">
                            <p className="admin-testManage-detail-text" style={{color: 'black'}}>
                                {latestContest.season > 0
                                    ? `현재 설정된 일정: ${latestContest.season}회차`
                                    : '개최된 대회가 없습니다'}
                            </p>

                            <div className="admin-testManage-contentbox">
                                {isDateModalOpen && (
                                    <div className="testManage-modal-overlay">
                                        <div className="testManage-modal-box">
                                            {/* X 버튼 */}
                                            <button
                                                className="testManage-modal-close"
                                                onClick={handleCloseModal}
                                            >
                                                X
                                            </button>

                                            <div className="testManage-modal-tabs">
                                                <button
                                                    onClick={() => setModalTab('접수')}
                                                    className={`testManage-modal-tab ${modalTab === '접수' ? 'active' : ''}`}
                                                >
                                                    접수 기간
                                                </button>
                                                <button
                                                    onClick={() => setModalTab('대회')}
                                                    className={`testManage-modal-tab ${modalTab === '대회' ? 'active' : ''}`}
                                                >
                                                    대회 기간
                                                </button>
                                            </div>

                                            {/* Tab별 내용 */}
                                            {modalTab === '접수' ? (
                                                <div className="testManage-modal-content">
                                                    <div>
                                                        <p>시작일</p>
                                                        <DatePicker
                                                            selected={tempRegisterStartDate}
                                                            onChange={(date) => setTempRegisterStartDate(date)}
                                                            selectsStart
                                                            startDate={tempRegisterStartDate}
                                                            endDate={tempRegisterEndDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                    </div>
                                                    <div>
                                                        <p>종료일</p>
                                                        <DatePicker
                                                            selected={tempRegisterEndDate}
                                                            onChange={(date) => setTempRegisterEndDate(date)}
                                                            selectsEnd
                                                            startDate={tempRegisterStartDate}
                                                            endDate={tempRegisterEndDate}
                                                            minDate={tempRegisterStartDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                    </div>
                                                </div>
                                            ) : (
                                                <div className="testManage-modal-content">
                                                    <div>
                                                        <p>시작일</p>
                                                        <DatePicker
                                                            selected={tempStartDate}
                                                            onChange={(date) => setTempStartDate(date)}
                                                            selectsStart
                                                            startDate={tempStartDate}
                                                            endDate={tempEndDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                    </div>
                                                    <div>
                                                        <p>종료일</p>
                                                        <DatePicker
                                                            selected={tempEndDate}
                                                            onChange={(date) => setTempEndDate(date)}
                                                            selectsEnd
                                                            startDate={tempStartDate}
                                                            endDate={tempEndDate}
                                                            minDate={tempStartDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                    </div>
                                                </div>
                                            )}

                                                <button
                                                    className="testManage-modal-confirm-btn"
                                                    onClick={handleConfirm}
                                                >
                                                    확인
                                                </button>
                                        </div>
                                    </div>
                                )}

                                <p className="admin-testManage-detail-text">
                                    <p style={{ color: 'black' }}>접수 기간</p>
                                    {registerStartDate && registerEndDate
                                        ? `${registerStartDate} ~ ${registerEndDate}`
                                        : '일정을 등록해주세요'}
                                </p>
                                <p className="admin-testManage-detail-text">
                                    <p style={{ color: 'black' }}>대회 기간</p>
                                    {startDate && endDate
                                        ? `${startDate} ~ ${endDate}`
                                        : '일정을 등록해주세요'}
                                </p>
                            </div>
                            <div className="admin-testManage-buttonbox">
                                <div className="admin-testManage-left-button"
                                     onClick={() => setIsDateModalOpen(true)}>등록하기
                                </div>
                                <div className="admin-testManage-left-button"
                                     style={{backgroundColor: 'lightblue', color: 'black'}}
                                     onClick={openEditModal}>수정하기
                                </div>
                                <div className="admin-testManage-right-button" onClick={handleDeleteDate}>삭제하기</div>
                            </div>
                        </div>
                    </div>
                    <div className="admin-testManage-detail-container">
                        <div className="admin-testManage-detail-top">
                            <p className="admin-testManage-detail-text" style={{color: '#345FEC'}}>문제
                                <span style={{color: 'black'}}> 등록</span></p>
                            <div className="admin-testManage-detail-underLine"></div>
                        </div>
                        <img src={testImage} alt="testImage" className="admin-testManage-image"/>
                        <form encType="multipart/form-data" onSubmit={handleRegisterSelectedTypes}>
                            <div className="admin-testManage-detail-bot">
                                <p className="admin-testManage-detail-text" style={{color: 'black'}}>현재 게시된 문제 (클릭 시 다운로드)</p>
                                <div className="admin-testManage-contentbox" style={{padding: '15px', height: '60%'}}>

                                    {/* 공통 문제 섹션 */}
                                    <div className="admin-testManage-content">
                                        {/* 체크박스 + 레이블 */}
                                        <input type="checkbox" className="admin-testManage-input"
                                               checked={checkedTypes['공통']} onChange={() => toggleTypeCheck('공통')}
                                        />
                                        <p className="admin-testManage-detail-text" style={{width: 200}}> (공통) /</p>

                                        {!commonTempFile && !commonQuiz[0]
                                            && <p className="admin-testManage-detail-text" style={{
                                                textAlign: 'left', width: 200, color: 'black'}}>등록되지 않음</p>
                                        }

                                        {/* 1) 이미 등록된 문제 목록 */}
                                        {!commonTempFile && commonQuiz.map(p => (
                                            <p
                                               key={p.problemId}
                                               onClick={() => handleDownload(p.problemId)}
                                               style={{
                                                   cursor: 'pointer',
                                                   whiteSpace: 'nowrap',
                                                   overflow: 'hidden',
                                                   textOverflow: 'ellipsis',
                                                   textAlign: 'left',
                                                   color: 'black',
                                                   width: 200
                                               }}
                                            >{p.title}</p>
                                        ))}

                                        {/* 2) 임시 첨부된 파일 (바로 다운로드 가능) */}
                                        {commonTempFile && (
                                            <a href={URL.createObjectURL(commonTempFile)}
                                               download={commonTempFile.name}
                                            >{commonTempFile.name}</a>
                                        )}

                                        {/* 3) 파일 입력 + 라벨 */}
                                        <input type="file" accept=".pdf"
                                               style={{display: 'none'}} id="file-upload-common"
                                               onChange={(e) => handleFileChange(e, setCommonTempFile)}
                                        />
                                        <label htmlFor="file-upload-common" className="quiz-file-button" style={{height: 'fit-content'}}>첨부</label>
                                    </div>

                                    {/* 초/중등 섹션 (같은 패턴) */}
                                    <div className="admin-testManage-content">
                                        <input type="checkbox" className="admin-testManage-input"
                                               checked={checkedTypes['초/중등']} onChange={() => toggleTypeCheck('초/중등')}
                                        />
                                        <p className="admin-testManage-detail-text" style={{width: 200}}>(초/중등) /</p>
                                        {!easyTempFile && !easyQuiz[0]
                                            && <p className="admin-testManage-detail-text" style={{
                                                textAlign: 'left', width: 200, color: 'black'}}>등록되지 않음</p>
                                        }
                                        {!easyTempFile && easyQuiz.map(p => (
                                            <p key={p.problemId} onClick={() => handleDownload(p.problemId)}
                                               style={{
                                                   cursor: 'pointer',
                                                   whiteSpace: 'nowrap',
                                                   overflow: 'hidden',
                                                   textOverflow: 'ellipsis',
                                                   textAlign: 'left',
                                                   color: 'black',
                                                   width: 200
                                               }}> {p.title} </p>
                                        ))}
                                        {easyTempFile && (
                                            <a href={URL.createObjectURL(easyTempFile)} download>{easyTempFile.name}</a>
                                        )}
                                        <input type="file" accept=".pdf" style={{display: 'none'}} id="file-upload-easy"
                                               onChange={(e) => handleFileChange(e, setEasyTempFile)}
                                        />
                                        <label htmlFor="file-upload-easy" className="quiz-file-button" style={{height: 'fit-content'}}>첨부</label>
                                    </div>

                                    {/* 고등/일반 섹션 */}
                                    <div className="admin-testManage-content">
                                        <input type="checkbox" className="admin-testManage-input"
                                               checked={checkedTypes['고등/일반']} onChange={() => toggleTypeCheck('고등/일반')}
                                        />
                                        <p className="admin-testManage-detail-text" style={{width: 200}}>(고등/일반) /</p>
                                        {!hardTempFile && !hardQuiz[0]
                                            && <p className="admin-testManage-detail-text" style={{
                                                textAlign: 'left', width: 200, color: 'black'}}>등록되지 않음</p>
                                        }
                                        {!hardTempFile && hardQuiz.map(p => (
                                            <p key={p.problemId} onClick={() => handleDownload(p.problemId)}
                                               style={{
                                                   cursor: 'pointer',
                                                   whiteSpace: 'nowrap',
                                                   overflow: 'hidden',
                                                   textOverflow: 'ellipsis',
                                                   textAlign: 'left',
                                                   color: 'black',
                                                   width: 200
                                               }}> {p.title} </p>
                                        ))}
                                        {hardTempFile && (
                                            <a href={URL.createObjectURL(hardTempFile)} download>{hardTempFile.name}</a>
                                        )}
                                        <input type="file" accept=".pdf" style={{display: 'none'}} id="file-upload-hard"
                                               onChange={(e) => handleFileChange(e, setHardTempFile)}
                                        />
                                        <label htmlFor="file-upload-hard" className="quiz-file-button" style={{height: 'fit-content'}}>첨부</label>
                                    </div>

                                </div>
                                <div className="admin-testManage-buttonbox">
                                    <button type="submit" className="admin-testManage-left-button">등록하기</button>
                                    <div className="admin-testManage-right-button"
                                         onClick={handleDeleteSelectedTypes}>삭제하기
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TestManage;