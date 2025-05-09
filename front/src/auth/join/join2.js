import React, {useEffect, useRef, useState} from 'react';
import './join2.css'
import SubHeader from "../../components/subHeader/subHeader";
import {useNavigate} from "react-router-dom";
import DaumPostcode from "react-daum-postcode";
import EmailVerificationModal from '../../components/modals/emailVerificationModal';
import apiClient from "../../templates/apiClient";
import SchoolSearchModal from "../../components/modals/schoolSearchModal";

const Join2 = () => {
    const navigate = useNavigate();
    /*-------------앞 페이지 항목 동의 여부------------*/
    useEffect(() => {
        const isChecked = sessionStorage.getItem("isChecked");
        if (isChecked !== "true") {
            navigate('/join/policy');
        }
    }, []);
    /*--------------아이디--------------*/
    const [userId, setUserId] = useState('');
    const [isDuplicate, setIsDuplicate] = useState(true);
    const [idErrorMessage, setIdErrorMessage] = useState('');
    /*--------------비밀번호--------------*/
    const [password, setPassword] = useState('');
    const [passwordCheck, setPasswordCheck] = useState('');
    const [passwordErrorMessage, setPasswordErrorMessage] = useState('');
    /*--------------이름--------------*/
    const [name, setName] = useState('');
    /*--------------생일--------------*/
    const [birthday, setBirthday] = useState('');
    /*--------------성별--------------*/
    const [gender, setGender] = useState('');
    /*--------------주소--------------*/
    const [postcode, setPostcode] = useState('');
    const [address, setAddress] = useState('');
    const [detailAddress, setDetailAddress] = useState('');
    const [extraAddress, setExtraAddress] = useState('');
    const [sido, setSido] = useState('');
    const [isPostcodeOpen, setIsPostcodeOpen] = useState(false);
    /*--------------휴대폰번호--------------*/
    const [prefix, setPrefix] = useState('010'); // 기본값 010
    const [middle, setMiddle] = useState('');
    const [last, setLast] = useState('');
    const middleInputRef = useRef(null);
    const lastInputRef = useRef(null);

    /*--------------이메일------------------*/
    const [email, setEmail] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    /*--------------직업------------------*/
    const [job, setJob] = useState('');
    //

    /*--------------학교(소속)------------------------*/
    const [workPlace, setWorkPlace] = useState('');
    const [schoolModalOpen, setSchoolModalOpen] = useState(false);
    const [selectedSchool, setSelectedSchool] = useState({
        schoolName: '',
        region: '',
        estType: ''
    });

    /*--------------학년(부서)------------------------*/
    const [detailJob, setDetailJob] = useState('');

    /*------------------- 회원가입 & 나가기 버튼 기능----------------*/
    const handleSignup = (e) => {
        if(job.startsWith('s')){
            setWorkPlace(selectedSchool.schoolName);
        }
        e.preventDefault();
        if(isDuplicate) {
            alert('아이디를 다시 확인해주세요.');
            return;
        }
        if(password !== passwordCheck){
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        if(!address){
            alert('주소를 등록해주세요.');
            return;
        }

        if(!detailAddress){
            alert('상세주소를 등록해주세요.');
            return;
        }

        if(!email){
            alert('인증을 통해 이메일을 등록해주세요.')
            return;
        }

        function changeBirth() {
            const year = parseInt(birthday.slice(0, 4), 10);
            const month = parseInt(birthday.slice(4, 6), 10) - 1;
            const day = parseInt(birthday.slice(6, 8), 10);
            return new Date(year, month, day);
        }

        apiClient.post('/api/v1/members', {
            loginId : userId,
            password,
            confirmPassword: passwordCheck,
            name,
            birth: changeBirth(),
            gender,
            street: address,
            city: sido,
            zipCode: postcode,
            detail: detailAddress,
            phoneNumber : prefix+middle+last,
            email,
            organizationType: job.slice(2),
            organizationName: workPlace,
            position: detailJob,
        }, )
            .then((res) => {
                if(res.data.code === 200){
                    //로그인 바로 진행
                    apiClient.post('/api/auth/login', {username: userId, password})
                        .then((res)=>{
                            navigate('/');
                        })
                        .catch((err)=>{})
                }
            })
            .catch((err)=>{
                }
            )
    }

    const handleExit = () => {
        navigate('/');
    };

    /*------------------- 아이디 기능 ----------------*/
    const handleIdChange = (e) => {
        setUserId(e.target.value);
        setIdErrorMessage('');
    }

    const handleIdCheck = () => {
        const idRegex = /^[a-zA-Z][a-zA-Z0-9]{3,9}$/;
        if (!idRegex.test(userId)) {
            setIsDuplicate(true);
            setIdErrorMessage('아이디는 영어로 시작하고\n영어와 숫자만 4~10자리로 입력해야 합니다.');
        }
        else {
            apiClient.get('/api/check-id', {
                params: {loginId: userId}})
                .then((res) => {
                    const result = res.data.data;
                    if (result) {
                        setIsDuplicate(true);
                        setIdErrorMessage('이미 사용 중인 아이디입니다.')
                    }
                    else {
                        setIsDuplicate(false);
                        setIdErrorMessage('');
                    }
                })
                .catch((err) => {})
        }
    }

    /*------------------- 비밀번호 기능 ----------------*/
    const handlePasswordChange = (e) => {
        const newPassword = e.target.value;
        setPassword(e.target.value);
        if(passwordCheck === newPassword)
            setPasswordErrorMessage('✅');
        else
            setPasswordErrorMessage('❌');
    }

    const handlePasswordChange2 = (e) => {
        const newPasswordCheck = e.target.value;
        setPasswordCheck(newPasswordCheck);
        if(password === newPasswordCheck)
            setPasswordErrorMessage('✅');
        else
            setPasswordErrorMessage('❌');
    }

    /*-------------------기타 input field 기능 ----------------*/
    const handleNameChange = (e) => {
        setName(e.target.value);
    }

    const handleBirthdayChange = (e) => {
        setBirthday(e.target.value);
    }

    const handleGenderChange = (e) => {
        setGender(e.target.value);
    };

    const handleJobChange = (e) => {
        setJob(e.target.value);
    }

    /*------------------- 주소찾기 기능 ----------------*/
    const handleComplete = (data) => {
        let fullAddress = data.address;
        let extraAddress = '';
        const sido = data.sido;

        if (data.addressType === 'R') {
            if (data.bname !== '') {
                extraAddress += data.bname;
            }
            if (data.buildingName !== '') {
                extraAddress += extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName;
            }
            fullAddress += extraAddress !== '' ? ` (${extraAddress})` : '';
        }

        setPostcode(data.zonecode);
        setAddress(fullAddress);
        setExtraAddress(extraAddress);
        setSido(sido);
        setIsPostcodeOpen(false);
    };

    const handlePostcodeOpen = () => {
        setIsPostcodeOpen(!isPostcodeOpen);
    };

    const postcodeStyle = {
        display: isPostcodeOpen ? 'block' : 'none',
        position: 'fixed',
        top: '50px',
        right: '10%',
        width: '35%',
        height: '70%',
        zIndex: '1000',
        backgroundColor: 'white',
        borderStyle: 'solid',
        borderColor: 'gray',
    };

    const closeButtonStyle = {
        position: 'absolute',
        top: '5px',
        right: '10px',
        fontSize: '20px',
        cursor: 'pointer',
    };

    /*------------------- 휴대폰번호 기능(post시 prefix, middle, last 합치기) ----------------*/
    const handleMiddleChange = (e) => {
        const value = e.target.value;
        setMiddle(value);
        if (value.length === 4) {
            lastInputRef.current.focus(); // 4자리 입력 시 다음 칸으로 이동
        }
    };

    const handleLastChange = (e) => {
        setLast(e.target.value);
    };

    /*------------------- 이메일 기능 ----------------*/

    const handleEmailCheck = () => {
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const handleVerifyEmail = (verifiedEmail) => {
        setEmail(verifiedEmail);
    };



    return (
        <div className="join2-page-container">
            <SubHeader />
            <div className="join2-content-container">
                <div className="join1-title">
                    <p className="join1-title-text">회원가입</p>
                    <div className="main-header-line" style={{width: '100%'}}></div>
                </div>
                <p className="join2-subtitle">아래 항목을 정확하게 입력해주세요.
                    <br />정확하지 않은 정보 입력으로 인한 불이익에 대해서는 책임지지 않습니다.
                    <br /><br />
                    <span style={{fontFamily: 'Roboto-Bold', fontSize: 14, color: '#FF9A26'}}>
                        * 표시 항목은 회원가입 시 꼭 필요한 필수 항목입니다.
                    <br />이메일 주소는 추후 아이디/비밀번호 찾기 및 접수 확인 시 기초 자료로 사용되오니, 정확하게 입력해주세요.
                    </span>
                </p>
                <form onSubmit={handleSignup} className="join2-main-container">
                    <div className="join2-main-upper">
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 아이디</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="text"
                                        value={userId}
                                        onChange={handleIdChange}
                                    />
                                    <button className="join2-id-button" type="button" onClick={handleIdCheck}>
                                        중복 확인
                                    </button>
                                    {idErrorMessage && <p className="info-message"
                                                          style={{color: 'red'}}>{idErrorMessage}</p>}
                                    {!isDuplicate && !idErrorMessage &&
                                        <p className="info-message" style={{color: 'green'}}>사용 가능한 아이디입니다.</p>}
                                </div>
                                <p className="join2-id-info">
                                    회원ID는 가입 후 바꾸실 수 없으니 신중하게 결정 해 주세요.
                                    <br/><span style={{color: '#2489DC'}}> 영문자로 시작하는 4~12자의 영문과 숫자를 사용하시고
                                </span> 여백없이 입력해 주세요.</p>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 비밀번호</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="password"
                                        value={password}
                                        onChange={handlePasswordChange}
                                        required
                                        minLength="4"
                                        maxLength="8"
                                        pattern="^[a-zA-Z][a-zA-Z0-9]{3,7}$"
                                    />
                                    <p className="info-message">영문자로 시작하는 4~8자의 영문과 숫자를 입력하세요.</p>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 비밀번호 확인</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="password"
                                        value={passwordCheck}
                                        onChange={handlePasswordChange2}
                                        required
                                        minLength="4"
                                        maxLength="8"
                                        pattern="^[a-zA-Z][a-zA-Z0-9]{3,7}$"
                                    />
                                    {password && <p className="info-message">{passwordErrorMessage}</p>}
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 이름</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="text"
                                        value={name}
                                        onChange={handleNameChange}
                                        minLength="2"
                                        required
                                        pattern="[가-힣a-zA-Z]+"
                                        title="한글 또는 영문자만 입력 가능합니다."
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 생일</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="text"
                                        value={birthday}
                                        onChange={handleBirthdayChange}
                                        maxLength="8"
                                        required
                                        pattern="\d{8}"
                                        title="YYYYMMDD 형식의 8자리 숫자를 입력하세요."
                                    />
                                    <p className="info-message">예) 19900301 (1990년 3월 1일)</p>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 성별</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <select
                                        className="join2-id-input"
                                        value={gender}
                                        onChange={handleGenderChange}
                                        required>
                                        <option value="">---</option>
                                        <option value="MAN">남자</option>
                                        <option value="WOMAN">여자</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 주소</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <input
                                        className="join2-id-input"
                                        type="text"
                                        id="sample6_postcode"
                                        value={postcode}
                                        readOnly
                                        placeholder="우편번호"
                                    />
                                    <button className="join2-id-button"
                                            type="button" onClick={handlePostcodeOpen}>
                                        조회하기
                                    </button>
                                </div>
                                <div className="join2-right-lower">
                                    <input type="text" id="sample6_address" placeholder="주소"
                                           value={address} readOnly
                                           className="join2-right-lower-top">

                                    </input>
                                    <div className="join2-right-lower-bot">
                                        <input type="text" id="sample6_detailAddress" placeholder="상세주소 입력"
                                               value={detailAddress}
                                               onChange={(e) => setDetailAddress(e.target.value)}
                                               className="join2-right-lower-bot-left">
                                        </input>
                                    </div>
                                </div>
                                {isPostcodeOpen && <div style={postcodeStyle}>
                                    <div style={{display: 'flex', flexDirection: 'column', height: '100%'}}>
                                        <div style={{
                                            height: '40px',
                                            backgroundColor: '#f0f0f0',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'space-between',
                                            padding: '0 10px'
                                        }}>
                                            <span>주소 검색</span>
                                            <span style={closeButtonStyle}
                                                  onClick={() => setIsPostcodeOpen(false)}>X</span>
                                        </div>
                                        <DaumPostcode onComplete={handleComplete} style={{flexGrow: 1}}/>
                                    </div>
                                </div>}

                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 휴대폰 번호</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row" style={{gap: '5px'}}>
                                    <select
                                        className="join2-id-input"
                                        style={{width: '10%'}}
                                        value={prefix}
                                        onChange={(e) => setPrefix(e.target.value)}
                                        required>
                                        <option value="010">010</option>
                                        <option value="011">011</option>
                                        <option value="016">016</option>
                                        <option value="017">017</option>
                                        <option value="018">018</option>
                                        <option value="019">019</option>
                                    </select>
                                    <p className="info-message2">-</p>
                                    <input
                                        className="join2-id-input"
                                        style={{width: '15%'}}
                                        type="tel"
                                        value={middle}
                                        onChange={handleMiddleChange}
                                        required
                                        maxLength="4"
                                        ref={middleInputRef}
                                        pattern="\d{4}"
                                        title="숫자만 입력하세요."
                                    />
                                    <p className="info-message2">-</p>
                                    <input
                                        className="join2-id-input"
                                        style={{width: '15%'}}
                                        type="tel"
                                        value={last}
                                        onChange={handleLastChange}
                                        required
                                        maxLength="4"
                                        ref={lastInputRef}
                                        pattern="\d{4}"
                                        title="숫자만 입력하세요."
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            {isModalOpen && <EmailVerificationModal onClose={handleCloseModal}
                            onVerify={handleVerifyEmail}/>}
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 이메일</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row" style={{gap: '5px'}}>
                                    <input
                                        className="join2-id-input"
                                        type="text"
                                        value={email}
                                        readOnly
                                    />
                                    <button className="join2-id-button" type="button" onClick={handleEmailCheck}>
                                        인증하기
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                <p className="join2-left-text">* 직업</p>
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    <select
                                        className="join2-id-input"
                                        value={job}
                                        onChange={handleJobChange}
                                        required>
                                        <option value="">---</option>
                                        <option value="s_초등학생">초등학생</option>
                                        <option value="s_중학생">중학생</option>
                                        <option value="s_고등학생">고등학생</option>
                                        <option value="s_대학생">대학생</option>
                                        <option value="p_컴퓨터/인터넷">컴퓨터/인터넷</option>
                                        <option value="p_언론">언론</option>
                                        <option value="p_공무원">공무원</option>
                                        <option value="p_군인">군인</option>
                                        <option value="p_서비스업">서비스업</option>
                                        <option value="p_예술">예술</option>
                                        <option value="p_기타">기타</option>

                                    </select>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                {!job && <p className="join2-left-text">* 학교(소속)</p>}
                                {job.startsWith('s') && <p className="join2-left-text">* 학교</p>}
                                {job.startsWith('p') && <p className="join2-left-text">* 소속</p>}
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    {!job && <input
                                        className="join2-id-input"
                                        readOnly
                                        value={" ---"}
                                        required/>
                                    }
                                    {job.startsWith('s') &&
                                        <>
                                            <input
                                                className="join2-id-input"
                                                value={selectedSchool?.schoolName}
                                                readOnly
                                                required>
                                            </input>
                                            <button className="join2-id-button"
                                                    type="button"
                                                    onClick={() => {
                                                        setSchoolModalOpen(true)
                                                    }}>학교 검색
                                            </button>
                                            <SchoolSearchModal isOpen={schoolModalOpen}
                                                               onClose={() => {
                                                                   setSchoolModalOpen(false)
                                                               }}
                                                               level={job}
                                                               onSelectSchool={(school) => setSelectedSchool(school)}/>
                                        </>}
                                    {job.startsWith('p') &&
                                        <input
                                            className="join2-id-input"
                                            value={workPlace}
                                            onChange={(e) => {
                                                setWorkPlace(e.target.value)
                                            }}
                                            required>
                                        </input>}
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left" style={{borderBottom: 'none'}}>
                                {!job && <p className="join2-left-text">* 학년(부서)</p>}
                                {job.startsWith('s') && <p className="join2-left-text">* 학년</p>}
                                {job.startsWith('p') && <p className="join2-left-text">* 부서</p>}
                            </div>
                            <div className="join2-main-border-right" style={{borderBottom: 'none'}}>
                                <div className="join2-right-row">
                                    {job && job.startsWith('s') ? (
                                        <select className="join2-id-input" value={detailJob}
                                                onChange={(e) => {
                                                    setDetailJob(e.target.value)
                                                }} required>
                                            <option value="">---</option>
                                            {job.startsWith('s_초등학생') && (
                                                <>
                                                    {[1, 2, 3, 4, 5, 6].map((grade) => (
                                                        <option key={grade} value={grade}>
                                                            {grade}학년
                                                        </option>
                                                    ))}
                                                </>
                                            )}
                                            {(job.startsWith('s_중학생') || job.startsWith('s_고등학생')) && (
                                                <>
                                                    {[1, 2, 3].map((grade) => (
                                                        <option key={grade} value={grade}>
                                                            {grade}학년
                                                        </option>
                                                    ))}
                                                </>
                                            )}
                                            {job.startsWith('s_대학생') && (
                                                <>
                                                    {[1, 2, 3, 4].map((grade) => (
                                                        <option key={grade} value={grade}>
                                                            {grade}학년
                                                        </option>
                                                    ))}
                                                    <option value="기타">기타</option>
                                                </>
                                            )}
                                        </select>
                                    ) : job && job.startsWith('p') ? (
                                        <input
                                            type="text"
                                            className="join2-id-input"
                                            value={detailJob}
                                            required
                                            onChange={(e) => {
                                                setDetailJob(e.target.value)
                                            }}
                                        />
                                    ) : (
                                        <select className="join2-id-input"
                                                required
                                                value={detailJob}
                                                onChange={(e) => {
                                                    setDetailJob(e.target.value)
                                                }}>
                                            <option value="">---</option>
                                        </select>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="join1-buttonbox-lower">
                        <button className="join1-button" type="submit">가입하기</button>
                        <button className="join1-button" type="button"
                                style={{background: 'lightgray', color: 'black'}}
                                onClick={handleExit}>나가기
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Join2;