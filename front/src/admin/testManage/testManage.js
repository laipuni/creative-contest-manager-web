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
import DeletedContestList from "../components/deletedContestList/deletedContestList";
import {useNavigate} from "react-router-dom";

const TestManage = () => {
    // --- 일정 관련 상태 ---
    const [latestContest, setLatestContest] = useState({ season: 15, contestId: null });
    const [prelimRegisterStartDate, setPrelimRegisterStartDate] = useState('');
    const [prelimRegisterEndDate, setPrelimRegisterEndDate] = useState('');
    const [prelimStartDate, setPrelimStartDate] = useState('');
    const [prelimEndDate, setPrelimEndDate] = useState('');
    const [tempFinalStartDate, setTempFinalStartDate] = useState('');
    const [tempFinalEndDate, setTempFinalEndDate] = useState('');
    const [finalLocation, setFinalLocation] = useState('');

    const [isDateModalOpen, setIsDateModalOpen] = useState(false);
    const [tempPrelimRegisterStartDate, setTempPrelimRegisterStartDate] = useState(null);
    const [tempPrelimRegisterEndDate, setTempPrelimRegisterEndDate] = useState(null);
    const [tempPrelimStartDate, setTempPrelimStartDate] = useState(null);
    const [tempPrelimEndDate, setTempPrelimEndDate] = useState(null);
    const [isEditMode, setIsEditMode] = useState(false);
    const [isRegistered, setIsRegistered] = useState(false);
    const [isProblemEdited, setIsProblemEdited] = useState(false);
    const [showRestoreModal, setShowRestoreModal] = useState(false); // 대회 복구 안내
    const [prelimSeason, setPrelimSeason] = useState(null);
    const [showDeletedListModal, setShowDeletedListModal] = useState(false);

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
    const navigate = useNavigate();

    //최초 랜더링 시 마지막 대회 정보 들고오기
    useEffect(() => {
        apiClient.get('/api/admin/contests/latest', {skipErrorHandler: true})
            .then((res) => {
                if(res.data.data){
                   setLatestContest({
                       season: res.data.data.season,
                       contestId: res.data.data.contestId});
                    setPrelimRegisterStartDate(formatDate(new Date(res.data.data.registrationStartAt)));
                    setPrelimRegisterEndDate(formatDate(new Date(res.data.data.registrationEndAt)));
                    setPrelimStartDate(formatDate(new Date(res.data.data.startTime)));
                    setPrelimEndDate(formatDate(new Date(res.data.data.endTime)));
                }
                else{
                    setLatestContest({
                        season: 15,
                        contestId: null
                    })
                    setPrelimRegisterStartDate(null);
                    setPrelimRegisterEndDate(null);
                    setPrelimStartDate(null);
                    setPrelimEndDate(null);
                }
            })
            .catch((err)=>{
                if(err.response.status === 401){
                    alert('권한이 없습니다.');
                    navigate('/');
                }
                else{
                    alert(err.response.data.message);
                }
            })
    }, [isRegistered])

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
    }, [latestContest.contestId, isProblemEdited])


    //일정 등록
    const handleRegisterDate = () => {
        if (!tempPrelimRegisterStartDate || !tempPrelimRegisterEndDate || !tempPrelimStartDate || !tempPrelimEndDate) {
            alert('접수 기간과 대회 기간의 시작일과 종료일을 모두 선택해주세요.');
            return;
        }

        if ((tempPrelimRegisterStartDate > tempPrelimRegisterEndDate) || (tempPrelimStartDate > tempPrelimEndDate)) {
            alert('종료일이 시작일보다 빠르게 설정되었습니다.');
            return;
        }

        const contestTitle = `${prelimSeason}회차 cps 경진대회 예선`;
        const finalContestTitle = `${prelimSeason}회차 cps 경진대회 본선`;
        apiClient.post('/api/admin/contests', {
            title: contestTitle, season: prelimSeason,
            registrationStartAt: toISOStringWithUTC9(tempPrelimRegisterStartDate), registrationEndAt: toISOStringWithUTC9(tempPrelimRegisterEndDate),
            contestStartAt: toISOStringWithUTC9(tempPrelimStartDate), contestEndAt: toISOStringWithUTC9(tempPrelimEndDate),
            finalContestTitle: finalContestTitle, finalContestLocation: finalLocation,
            finalContestStartTime: toISOStringWithUTC9(tempFinalStartDate), finalContestEndTime: toISOStringWithUTC9(tempFinalEndDate),
        }, {skipErrorHandler: true})
            .then((res) => {
                setIsDateModalOpen(false);
                setIsRegistered(!isRegistered);
                alert('대회가 등록되었습니다');
            })
            .catch((err)=>{
                if(err.response.data.message === '동일한 회의 대회가 있습니다.') {
                    apiClient.get('/api/admin/contests/deleted')
                        .then((res) => {
                            const matchedContest = res.data.data.deletedContestList.find(
                                (contest) => contest.season === Number(prelimSeason)
                            )
                            if(matchedContest)
                                setShowRestoreModal(true);
                            else
                                alert('해당 회차의 대회가 이미 존재합니다.')
                        })
                        .catch((err)=>{})

                }
                else{
                    alert(err.response.data.message);
                }
            })
    };

    //일정 수정
    const openEditModal = () => {
        if(latestContest.season <= 0){
            alert('등록을 먼저 해주세요');
            return;
        }
        const parseDate = (d) =>
            typeof d === 'string' ? new Date(d.replace(/\./g, '-')) : d;

        setPrelimSeason(Number(latestContest.season));
        setTempPrelimRegisterStartDate(parseDate(prelimRegisterStartDate));
        setTempPrelimRegisterEndDate(parseDate(prelimRegisterEndDate));
        setTempPrelimStartDate(parseDate(prelimStartDate));
        setTempPrelimEndDate(parseDate(prelimEndDate));
        setIsEditMode(true);
        setIsDateModalOpen(true);
    };

    const handleConfirm = () => {
        if (isEditMode) {
            if ((tempPrelimStartDate > tempPrelimEndDate) || (prelimStartDate > prelimEndDate)) {
                alert('종료일이 시작일보다 빠르게 설정되었습니다.');
                return;
            }

            const contestTitle = `${prelimSeason}회차 cps 경진대회 예선`;
            const finalContestTitle = `${prelimSeason}회차 cps 경진대회 본선`;
            apiClient.put('/api/admin/contests', {
                title: contestTitle, season: prelimSeason, contestId: latestContest.contestId,
                registrationStartAt: toISOStringWithUTC9(tempPrelimRegisterStartDate), registrationEndAt: toISOStringWithUTC9(tempPrelimRegisterEndDate),
                contestStartAt: toISOStringWithUTC9(tempPrelimStartDate), contestEndAt: toISOStringWithUTC9(tempPrelimEndDate),
                finalContestTitle: finalContestTitle, finalContestLocation: finalLocation,
                finalContestStartTime: toISOStringWithUTC9(tempFinalStartDate), finalContestEndTime: toISOStringWithUTC9(tempFinalEndDate),
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
            setPrelimSeason('');
            setTempPrelimRegisterStartDate(null);
            setTempPrelimRegisterEndDate(null);
            setTempPrelimStartDate(null);
            setTempPrelimEndDate(null);
            setTempFinalStartDate(null);
            setTempFinalEndDate(null);
            setIsEditMode(false);
        }
        setIsDateModalOpen(false);
    };


    // 날짜 포맷을 'yyyy.MM.dd HH:mm'로 변환
    const formatDate = (date) => {
        if (typeof date === 'string') {
            // 이미 포맷처리 된 경우
            return date;
        }
        if (date instanceof Date && !isNaN(date)) {
            const y = date.getFullYear();
            const m = String(date.getMonth() + 1).padStart(2, '0');
            const d = String(date.getDate()).padStart(2, '0');
            const h = String(date.getHours()).padStart(2, '0');
            const min = String(date.getMinutes()).padStart(2, '0');
            return `${y}.${m}.${d} ${h}:${min}`;
        }
    };


    // 날짜를 UTC+9 기준 ISO 문자열로 변환
    const toISOStringWithUTC9 = (value) => {
        if (typeof value === 'string') {
            // 문자열 -> Date로 파싱 (format: yyyy.MM.dd or yyyy.MM.dd HH:mm)
            const [datePart, timePart = '00:00'] = value.split(' ');
            const [year, month, day] = datePart.split('.').map(Number);
            const [hour, minute] = timePart.split(':').map(Number);

            if (!year || !month || !day) return value;

            const localDate = new Date(year, month - 1, day, hour || 0, minute || 0);
            const utcDate = new Date(localDate.getTime() + 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        if (value instanceof Date) {
            const utcDate = new Date(value.getTime() + 9 * 60 * 60 * 1000);
            return utcDate.toISOString();
        }

        return value;
    };




    //일정 삭제 - soft
    const handleDeleteDate = () => {
        apiClient.delete('/api/admin/contests', {
            data: { contestId: latestContest.contestId },})
            .then((res)=>{
                alert('삭제 완료');
                setIsRegistered(!isRegistered);
            })
            .catch((err)=>{})
    }

    //일정 복구
    const handleRestore = (contestId) => {
        return apiClient.patch(`/api/admin/contests/${contestId}/recover`, {skipErrorHandler: true})
            .then(() => {
                alert('복구 완료');
                setIsDateModalOpen(false);
                setShowRestoreModal(false);
                setIsRegistered(prev => !prev);
            })
    };



    //일정 삭제 - hard
    const handleHardDelete = (contestId) => {
        return apiClient.delete('/api/admin/contests/hard', {
                data: {contestId}, skipErrorHandler: true
            })
                .then((res) => {
                    alert('삭제 완료');
                    setIsRegistered(!isRegistered);
                })
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
            setIsProblemEdited(!isProblemEdited);
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
            setIsProblemEdited(!isProblemEdited);
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
                            {showDeletedListModal && (
                                <div
                                    className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
                                    <DeletedContestList
                                        onRestore={handleRestore}
                                        onHardDelete={handleHardDelete}
                                        onClose={() => setShowDeletedListModal(false)}
                                    />
                                </div>
                            )}

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
                                                                onClick={(e)=>{handleRestore(prelimSeason)}}>예
                                                        </button>
                                                        <button className="testmanage-restore-modal-button"
                                                                onClick={(e)=>{handleHardDelete(prelimSeason)}}>아니오
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                        <div className="testManage-modal-box">
                                            <button className="testManage-modal-close" onClick={handleCloseModal}>X
                                            </button>

                                            {/* ✅ 예선 영역 */}
                                            <div className="testManage-section">
                                                <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px'}}>
                                                    <h3 className="testManage-section-title" style={{margin: 0}}>예선</h3>
                                                    <span style={{color: 'red', fontSize: '14px'}}>(필수선택)</span>
                                                </div>

                                                <div className="testManage-modal-content">
                                                    <p className="testManage-label">회차</p>
                                                    <input value={prelimSeason}
                                                           onChange={(e) => setPrelimSeason(e.target.value.replace(/[^0-9]/g, ''))}/>
                                                </div>

                                                <div className="testManage-modal-content">
                                                    <p className="testManage-label">접수기간</p>
                                                    <div className="testManage-date-row">
                                                        <DatePicker selected={tempPrelimRegisterStartDate}
                                                                    onChange={setTempPrelimRegisterStartDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    showTimeSelect/>
                                                        <span className="testManage-tilde">~</span>
                                                        <DatePicker selected={tempPrelimRegisterEndDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    onChange={setTempPrelimRegisterEndDate} showTimeSelect/>
                                                    </div>
                                                </div>

                                                <div className="testManage-modal-content">
                                                    <p className="testManage-label">대회기간</p>
                                                    <div className="testManage-date-row">
                                                        <DatePicker selected={tempPrelimStartDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    onChange={setTempPrelimStartDate} showTimeSelect/>
                                                        <span className="testManage-tilde">~</span>
                                                        <DatePicker selected={tempPrelimEndDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    onChange={setTempPrelimEndDate}
                                                                    showTimeSelect/>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* ✅ 구분선 */}
                                            <hr className="testManage-divider"/>

                                            {/* ✅ 본선 영역 */}
                                            <div className="testManage-section">
                                                <h3 className="testManage-section-title">본선</h3>

                                                <div className="testManage-modal-content">
                                                    <p className="testManage-label">장소</p>
                                                    <input type="text" value={finalLocation}
                                                           onChange={(e) => setFinalLocation(e.target.value)}/>
                                                </div>


                                                <div className="testManage-modal-content">
                                                    <p className="testManage-label">대회기간</p>
                                                    <div className="testManage-date-row">
                                                        <DatePicker selected={tempFinalStartDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    onChange={setTempFinalStartDate} showTimeSelect/>
                                                        <span className="testManage-tilde">~</span>
                                                        <DatePicker selected={tempFinalEndDate}
                                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                                    onChange={setTempFinalEndDate}
                                                                    showTimeSelect/>
                                                    </div>
                                                </div>

                                            </div>

                                            <button className="testManage-modal-confirm-btn"
                                                    onClick={handleConfirm}>확인
                                            </button>
                                        </div>
                                    </div>
                                )}

                                <p className="admin-testManage-detail-text">
                                    <p style={{color: 'black'}}>접수 기간</p>
                                    {prelimRegisterStartDate && prelimRegisterEndDate
                                        ? `${prelimRegisterStartDate} ~ ${prelimRegisterEndDate}`
                                        : '일정을 등록해주세요'}
                                </p>
                                <p className="admin-testManage-detail-text">
                                    <p style={{color: 'black'}}>대회 기간</p>
                                    {prelimStartDate && prelimEndDate
                                        ? `${prelimStartDate} ~ ${prelimEndDate}`
                                        : '일정을 등록해주세요'}
                                </p>
                            </div>
                            <button onClick={() => setShowDeletedListModal(true)}>
                                🗑️ 삭제된 대회 보기
                            </button>
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
                                <p className="admin-testManage-detail-text" style={{color: 'black'}}>현재 게시된 문제 (클릭 시
                                    다운로드)</p>
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