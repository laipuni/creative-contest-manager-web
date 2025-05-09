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
    const [latestContest, setLatestContest] = useState({ season: 15, contestId: null });
    const [registerStartDate, setRegisterStartDate] = useState('');
    const [registerEndDate, setRegisterEndDate] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [isDateModalOpen, setIsDateModalOpen] = useState(false);
    const [tempRegisterStartDate, setTempRegisterStartDate] = useState(null);
    const [tempRegisterEndDate, setTempRegisterEndDate] = useState(null);
    const [tempStartDate, setTempStartDate] = useState(null);
    const [tempEndDate, setTempEndDate] = useState(null);
    const [isEditMode, setIsEditMode] = useState(false);
    const [isRegistered, setIsRegistered] = useState(false);
    const [isDeleted, setIsDeleted] = useState(false);
    const [showRestoreModal, setShowRestoreModal] = useState(false); // 대회 복구 안내

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
                        season: 15,
                        contestId: null
                    })
                    setRegisterStartDate(null);
                    setRegisterEndDate(null);
                    setStartDate(null);
                    setEndDate(null);
                }
            })
            .catch((err)=>{})
    }, [isRegistered], [isDeleted])

    //최초 랜더링 or 대회 삭제됐을 때 문제 갱신
    useEffect(()=>{
        if(!latestContest.contestId)
            return;
        apiClient.get(`/api/admin/v1/contests/${latestContest.contestId}/problems`, {
            parameter: {page : 0}})
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
                setIsDateModalOpen(false);
                setIsRegistered(!isRegistered);
                alert('대회가 등록되었습니다');
            })
            .catch((err)=>{
                if(err.response.data.message === '동일한 회의 대회가 있습니다.')
                    setShowRestoreModal(true);
            })
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
                    alert('대회가 수정되었습니다');
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
            const utcDate = new Date(localDate.getTime() + 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        // Date 객체인 경우
        if (value instanceof Date) {
            const utcDate = new Date(value.getTime() + 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        // 다른 타입이면 그대로 반환
        return value;
    };




    //일정 삭제 - soft
    const handleDeleteDate = () => {
        apiClient.delete('/api/admin/contests', {
            data: { contestId: latestContest.contestId }})
            .then((res)=>{
                setIsRegistered(!isRegistered);
            })
            .catch((err)=>{})
    }

    //일정 복구
    const handleRestore = () => {
        apiClient.get('/api/admin/contests/deleted')
            .then((res) => {
                const nextSeason = latestContest.season + 1;
                const matchedContest = res.data.data.deletedContestList.find(
                    (contest) => contest.season === nextSeason
                );
                const matchedContestId = matchedContest?.contestId;
                apiClient.patch(`/api/admin/contests/${matchedContestId}/recover`)
                    .then((res) => {
                        alert('복구 완료');
                        setIsDateModalOpen(false);
                        setShowRestoreModal(false);
                        setIsRegistered(!isRegistered);
                    })
                    .catch((err)=>{})
            })
    }

    //일정 삭제 - hard
    const handleHardDelete = () => {

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
            setIsRegistered(r => !r);
            alert('문제가 등록되었습니다.');
            setCheckedTypes({
                '초/중등': false,
                '공통': false,
                '고등/일반': false
            });
        } catch (error) {
            console.error(error);
            alert('서버에 중복된 제목의 문제가 등록되어 있습니다.');
        }
    };




    // 문제 다운로드
    const handleDownload = async (problem) => {
        try {
            const res = await apiClient.get(`/api/admin/v1/contests/${latestContest.contestId}/problems/${problem.problemId}`);
            const fileId = res.data.data.fileList[0]?.fileId;

            if (fileId) {
                const fileResponse = await apiClient.get(`/api/admin/files/${fileId}`, {
                    responseType: 'blob',
                });

                const blob = new Blob([fileResponse.data]);
                const url = window.URL.createObjectURL(blob);

                const link = document.createElement('a');
                link.href = url;
                link.download = problem.title; // 파일명 설정
                document.body.appendChild(link);
                link.click();
                link.remove();

                window.URL.revokeObjectURL(url);
            } else {
                alert('파일이 존재하지 않습니다.');
            }
        } catch (err) {
        }
    };


    //선택된 문제 삭제하기
    const handleDeleteSelectedTypes = async () => {
        const sectionMap = {
            '공통': { quiz: commonQuiz, temp: commonTempFile, reset: () => setCommonQuiz([]) },
            '초/중등': { quiz: easyQuiz, temp: easyTempFile, reset: () => setEasyQuiz([]) },
            '고등/일반': { quiz: hardQuiz, temp: hardTempFile, reset: () => setHardQuiz([]) },
        };

        const typesToDelete = Object.keys(checkedTypes).filter(type => checkedTypes[type]);

        if (typesToDelete.length === 0) {
            alert('항목을 선택해주세요');
            return;
        }

        const invalidTypes = typesToDelete.filter(type => {
            const { quiz, temp } = sectionMap[type];
            return !quiz[0]?.problemId && !temp;
        });

        if (invalidTypes.length > 0) {
            alert(`삭제할 수 있는 문제만 선택해주세요.\n(${invalidTypes.join(', ')})`);
            return;
        }

        const deletePromises = [];

        typesToDelete.forEach(type => {
            const { quiz, temp, reset } = sectionMap[type];
            const problemId = quiz[0]?.problemId;

            // temp는 없고, 등록된 문제만 있는 경우 삭제 API 호출
            if (!temp && problemId) {
                deletePromises.push(
                    axios.delete(`/api/admin/contests/problems`, {
                        data: { deleteProblemId: problemId }
                    })
                );
            }

            // 상태 초기화
            reset();
        });

        try {
            await Promise.all(deletePromises);
            alert('삭제되었습니다.');
        } catch (error) {
            console.error(error);
            alert('삭제 중 오류가 발생했습니다.');
        }

        setIsRegistered(r => !r);
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
                <AdminSidebar height='817px'/>
                <div className="admin-testManage-main-container">
                    <div className="admin-testManage-detail-container">
                        <div className="admin-testManage-detail-top">
                            <p className="admin-testManage-detail-text">일정 <span style={{ color: 'black' }}>등록</span></p>
                            <div className="admin-testManage-detail-underLine"></div>
                        </div>
                        <img src={planImage} alt="planImage" className="admin-testManage-image" />
                        <div className="admin-testManage-detail-bot">
                            <p className="admin-testManage-detail-text" style={{color: 'black'}}>
                                {latestContest.contestId
                                    ? `현재 설정된 일정: ${latestContest.season}회차`
                                    : '개최된 대회가 없습니다'}
                            </p>

                            <div className="admin-testManage-contentbox">
                                {isDateModalOpen && (
                                    <div className="testManage-modal-overlay">
                                        {/* 대회 복구 내용 */}
                                        {showRestoreModal && (
                                            <div className="testmanage-restore-modal-overlay">
                                                <div className="testmanage-restore-modal-box" style={{position: 'relative'}}>
                                                    <button className="testManage-modal-close"
                                                            onClick={(e)=>{setShowRestoreModal(false)}}>X
                                                    </button>

                                                    <p className="testmanage-restore-modal-message">삭제된 최신 대회가 있습니다.
                                                        복구하시겠습니까?<br/><p style={{color: 'red'}}>※ 아니오 선택 시 영구 삭제됩니다.</p>
                                                    </p>
                                                    <div className="testmanage-restore-modal-buttons">
                                                        <button className="testmanage-restore-modal-button"
                                                                onClick={handleRestore}>예
                                                        </button>
                                                        <button className="testmanage-restore-modal-button"
                                                                onClick={handleHardDelete}>아니오
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                        <div className="testManage-modal-box">
                                            {/* X 버튼 */}
                                            <button className="testManage-modal-close" onClick={handleCloseModal}>X
                                            </button>

                                            {/* 날짜 입력 영역 */}
                                            <div className="testManage-modal-content">
                                                <div className="testManage-date-group">
                                                    <p className="testManage-label">접수기간</p>
                                                    <div className="testManage-date-row">
                                                        <DatePicker
                                                            selected={tempRegisterStartDate}
                                                            onChange={(date) => setTempRegisterStartDate(date)}
                                                            selectsStart
                                                            startDate={tempRegisterStartDate}
                                                            endDate={tempRegisterEndDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                        <span className="testManage-tilde">~</span>
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

                                                <div className="testManage-date-group">
                                                    <p className="testManage-label">대회기간</p>
                                                    <div className="testManage-date-row">
                                                        <DatePicker
                                                            selected={tempStartDate}
                                                            onChange={(date) => setTempStartDate(date)}
                                                            selectsStart
                                                            startDate={tempStartDate}
                                                            endDate={tempEndDate}
                                                            dateFormat="yyyy.MM.dd"
                                                        />
                                                        <span className="testManage-tilde">~</span>
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
                                            </div>

                                            {/* 확인 버튼 */}
                                            <button className="testManage-modal-confirm-btn" onClick={handleConfirm}>
                                                확인
                                            </button>
                                        </div>
                                    </div>
                                )}

                                <p className="admin-testManage-detail-text">
                                    <p style={{color: 'black'}}>접수 기간</p>
                                    {registerStartDate && registerEndDate
                                        ? `${registerStartDate} ~ ${registerEndDate}`
                                        : '일정을 등록해주세요'}
                                </p>
                                <p className="admin-testManage-detail-text">
                                    <p style={{color: 'black'}}>대회 기간</p>
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
                                               onClick={() => handleDownload(p)}
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
                                            <p key={p.problemId} onClick={() => handleDownload(p)}
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
                                            <p key={p.problemId} onClick={() => handleDownload(p)}
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