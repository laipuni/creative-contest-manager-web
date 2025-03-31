import React, {useRef, useState} from 'react';
import './join2.css'
import SubHeader from "../components/subHeader/subHeader";
import {useNavigate} from "react-router-dom";
import DaumPostcode from "react-daum-postcode";

const Join2 = () => {
    /*--------------아이디--------------*/
    const [userId, setUserId] = useState('');
    const [isDuplicate, setIsDuplicate] = useState(true);
    const [idErrorMessage, setIdErrorMessage] = useState('');
    const exampleIds = ['user1', 'user2'];
    /*--------------비밀번호--------------*/
    const [password, setPassword] = useState('');
    const [passwordCheck, setPasswordCheck] = useState('');
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
    const [isPostcodeOpen, setIsPostcodeOpen] = useState(false);
    /*--------------휴대폰번호--------------*/
    const [prefix, setPrefix] = useState('010'); // 기본값 010
    const [middle, setMiddle] = useState('');
    const [last, setLast] = useState('');
    const middleInputRef = useRef(null);
    const lastInputRef = useRef(null);
    //
    const navigate = useNavigate();

    /*------------------- 회원가입 & 나가기 버튼 기능----------------*/
    const handleSignup = (e) => {
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


        navigate('/');

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
        else if(exampleIds.includes(userId)) {
            setIsDuplicate(true);
            setIdErrorMessage('이미 사용 중인 아이디입니다.')
        }
        else {
            setIsDuplicate(false);
            setIdErrorMessage('');
        }
    }

    /*------------------- 비밀번호 기능 ----------------*/
    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    }

    const handlePasswordChange2 = (e) => {
        setPasswordCheck(e.target.value);
    }

    /*-------------------기타 input field 기능 ----------------*/
    const handleNameChange = (e) => {
        setName(e.target.value);
    }

    const handleBirthdayChange = (e) => {
        setBirthday(e.target.value);
    }

    const handleGenderChange = (event) => {
        setGender(event.target.value);
    };

    /*------------------- 주소찾기 기능 ----------------*/
    const handleComplete = (data) => {
        let fullAddress = data.address;
        let extraAddress = '';

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
                                        <option value="male">남성</option>
                                        <option value="femaie">여성</option>
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
                                <div className="join2-right-row">
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
                    </div>
                    <div className="join1-buttonbox-lower">
                        <button className="join1-button" type="submit">가입하기</button>
                        <button className="join1-button"
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