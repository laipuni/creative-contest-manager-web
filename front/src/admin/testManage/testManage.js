import React, { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import './testManage.css';
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import planImage from "../../styles/images/admin_register_day.png";
import testImage from "../../styles/images/admin_register_problem.png";

const TestManage = () => {
    const [checkedTypes, setCheckedTypes] = useState({
        '초/중등': false,
        '공통': false,
        '고등/일반': false
    });
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [commonQuiz, setCommonQuiz] = useState(null);
    const [easyQuiz, setEasyQuiz] = useState(null);
    const [hardQuiz, setHardQuiz] = useState(null);
    const [isDateModalOpen, setIsDateModalOpen] = useState(false); // 달력 모달
    const [tempStartDate, setTempStartDate] = useState(null); // 선택용 임시 날짜
    const [tempEndDate, setTempEndDate] = useState(null);
    const [tempRegisterStartDate, setTempRegisterStartDate] = useState(null); // 선택용 임시 날짜
    const [tempRegisterEndDate, setTempRegisterEndDate] = useState(null);
    const [registerStartDate, setRegisterStartDate] = useState('');
    const [registerEndDate, setRegisterEndDate] = useState('');
    const [modalTab, setModalTab] = useState('접수'); // 모달 탭 상태 관리
    const [isEditMode, setIsEditMode] = useState(false); // 일정 등록/수정 여부

    //최초 랜더링 시 예선 기간 및 문제 들고오기
    useEffect(() => {
        setRegisterStartDate('2025.04.03');
        setRegisterEndDate('2025.05.02');
        setStartDate('2025.05.03');
        setEndDate('2025.06.03');
    }, [])

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
        setRegisterStartDate(formatDate(tempRegisterStartDate));
        setRegisterEndDate(formatDate(tempRegisterEndDate));
        setStartDate(formatDate(tempStartDate));
        setEndDate(formatDate(tempEndDate));
    };

    //일정 수정
    const openEditModal = () => {
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

            setRegisterStartDate(formatDate(rStart));
            setRegisterEndDate(formatDate(rEnd));
            setStartDate(formatDate(cStart));
            setEndDate(formatDate(cEnd));
        } else {
            handleRegisterDate();
        }

        setIsDateModalOpen(false);
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


    //일정 삭제
    const handleDeleteDate = () => {
        setStartDate('');
        setEndDate('');
    }

    //문제 체크박스 선택
    const toggleTypeCheck = (type) => {
        setCheckedTypes(prev => ({
            ...prev,
            [type]: !prev[type]
        }));
    };

    //선택된 문제 등록하기
    const handleRegisterSelectedTypes = () => {
        const typesToDelete = Object.keys(checkedTypes).filter(type => checkedTypes[type]);
        console.log(typesToDelete);
        if (typesToDelete.length < 1) {
            alert('항목을 선택해주세요');
        }
        //등록 api 연동
    }

    //선택된 문제 삭제하기
    const handleDeleteSelectedTypes = () => {
        const typesToDelete = Object.keys(checkedTypes).filter(type => checkedTypes[type]);
        console.log(typesToDelete);
        if (typesToDelete.length < 1) {
            alert('항목을 선택해주세요');
        }
        // 데이터 초기화
        typesToDelete.forEach(type => {
            if (type === '초/중등') {
                setEasyQuiz(null);
            } else if (type === '공통') {
                setCommonQuiz(null);
            } else if (type === '고등/일반') {
                setHardQuiz(null);
            }
        });

        // 체크 상태 초기화
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
                            <p className="admin-testManage-detail-text" style={{ color: 'black' }}>현재 설정된 일정</p>
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
                        <div className="admin-testManage-detail-bot">
                            <p className="admin-testManage-detail-text" style={{color:'black'}}>현재 게시된 문제</p>
                            <div className="admin-testManage-contentbox" style={{padding: '15px', height:'60%'}}>
                                <div className="admin-testManage-content">
                                    <input
                                        className="admin-testManage-input"
                                        type="checkbox"
                                        checked={checkedTypes['공통']}
                                        onChange={() => toggleTypeCheck('공통')}
                                    />
                                    <p className="admin-testManage-detail-text"
                                       style={{textAlign: 'left', width: '200px'}}>(공통) /</p>
                                    {!commonQuiz &&
                                        <p className="admin-testManage-detail-text" style={{
                                            color: 'black', textAlign: 'left'
                                        }}>등록된 문제 없음</p>}
                                    {commonQuiz && (
                                        <>
                                            <a
                                                href={URL.createObjectURL(commonQuiz)}
                                                download={commonQuiz.name}
                                                style={{
                                                    display: 'inline-block',
                                                    marginTop: '10px',
                                                    width: '100%',
                                                    overflow: 'hidden',
                                                    whiteSpace: 'nowrap',
                                                    textOverflow: 'ellipsis',
                                                    color: '#000000',
                                                    fontFamily: 'Roboto',
                                                    fontWeight: 400,
                                                    fontSize: '16px',
                                                }}
                                            >
                                                {commonQuiz.name}
                                            </a>
                                        </>
                                    )}
                                    <input
                                        type="file"
                                        accept=".pdf"
                                        onChange={(e) => handleFileChange(e, setCommonQuiz)}
                                        className="quiz-filename" id="file-upload-easy"
                                        style={{display: "none"}}
                                    />
                                    {/* 사용자에게 보일 버튼 */}
                                    <label htmlFor="file-upload-easy" className="quiz-file-button"
                                           style={{width: '10%', height: '35%', marginTop: '10px'}}>
                                        파일 등록
                                    </label>
                                </div>
                                <div className="admin-testManage-content">
                                    <input
                                        className="admin-testManage-input"
                                        type="checkbox"
                                        checked={checkedTypes['초/중등']}
                                        onChange={() => toggleTypeCheck('초/중등')}
                                    />
                                    <p className="admin-testManage-detail-text"
                                       style={{textAlign: 'left', width: '200px'}}>(초/중등) /</p>
                                    {!easyQuiz &&
                                        <p className="admin-testManage-detail-text" style={{
                                            color: 'black', textAlign: 'left'
                                        }}>등록된 문제 없음</p>}
                                    {easyQuiz && (
                                        <>
                                            <a
                                                href={URL.createObjectURL(easyQuiz)}
                                                download={easyQuiz.name}
                                                style={{
                                                    display: 'inline-block',
                                                    marginTop: '10px',
                                                    width: '100%',
                                                    overflow: 'hidden',
                                                    whiteSpace: 'nowrap',
                                                    textOverflow: 'ellipsis',
                                                    color: '#000000',
                                                    fontFamily: 'Roboto',
                                                    fontWeight: 400,
                                                    fontSize: '16px',
                                                }}
                                            >
                                                {easyQuiz.name}
                                            </a>
                                        </>
                                    )}
                                    <input
                                        type="file"
                                        accept=".pdf"
                                        onChange={(e) => handleFileChange(e, setEasyQuiz)}
                                        className="quiz-filename" id="file-upload-hard"
                                        style={{display: "none"}}
                                    />
                                    {/* 사용자에게 보일 버튼 */}
                                    <label htmlFor="file-upload-hard" className="quiz-file-button"
                                           style={{width: '10%', height: '35%', marginTop: '10px'}}>
                                        파일 등록
                                    </label>
                                </div>
                                <div className="admin-testManage-content">
                                    <input
                                        className="admin-testManage-input"
                                        type="checkbox"
                                        checked={checkedTypes['고등/일반']}
                                        onChange={() => toggleTypeCheck('고등/일반')}
                                    />
                                    <p className="admin-testManage-detail-text"
                                       style={{textAlign: 'left', width: '200px'}}>(고등/일반) /</p>
                                    {!hardQuiz &&
                                        <p className="admin-testManage-detail-text" style={{
                                            color: 'black', textAlign: 'left'
                                        }}>등록된 문제 없음</p>}
                                    {hardQuiz && (
                                        <>
                                            <a
                                                href={URL.createObjectURL(hardQuiz)}
                                                download={hardQuiz.name}
                                                style={{
                                                    display: 'inline-block',
                                                    marginTop: '10px',
                                                    width: '100%',
                                                    overflow: 'hidden',
                                                    whiteSpace: 'nowrap',
                                                    textOverflow: 'ellipsis',
                                                    color: '#000000',
                                                    fontFamily: 'Roboto',
                                                    fontWeight: 400,
                                                    fontSize: '16px',
                                                }}
                                            >
                                                {hardQuiz.name}
                                            </a>
                                        </>
                                    )}
                                    <input
                                        type="file"
                                        accept=".pdf"
                                        onChange={(e) => handleFileChange(e, setHardQuiz)}
                                        className="quiz-filename" id="file-upload-common"
                                        style={{display: "none"}}
                                    />
                                    {/* 사용자에게 보일 버튼 */}
                                    <label htmlFor="file-upload-common" className="quiz-file-button"
                                           style={{width: '10%', height: '35%', marginTop: '10px'}}>
                                        파일 등록
                                    </label>
                                </div>
                            </div>
                            <div className="admin-testManage-buttonbox">
                                <div className="admin-testManage-left-button"
                                     onClick={handleRegisterSelectedTypes}>등록하기</div>
                                <div className="admin-testManage-right-button"
                                     onClick={handleDeleteSelectedTypes}>삭제하기
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TestManage;