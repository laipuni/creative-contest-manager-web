import React, {useEffect, useRef, useState} from 'react';
import SubHeader from "../../components/subHeader/subHeader";
import DaumPostcode from "react-daum-postcode";
import EmailVerificationModal from "../../components/modals/emailVerificationModal";
import SchoolSearchModal from "../../components/modals/schoolSearchModal";
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";
import MainHeader from "../../components/mainHeader/mainHeader";

const MyProfile = () => {
    const navigate = useNavigate();
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
    const isSchool = ["초등학생", "중학생", "고등학생", "대학생"];
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

    /*-----------------정보 불러오기-------------*/
    useEffect(() => {
        apiClient.get('/api/members/my-profile')
            .then((res) => {
                const profile = res.data.data;
                setName(profile.name);
                setBirthday(profile.birth.replace(/-/g, ""));
                setPostcode(profile.zipCode);
                setAddress(profile.street);
                setEmail(profile.email);
                setPrefix(profile.phoneNumber.slice(0,3));
                setMiddle(profile.phoneNumber.slice(3,7));
                setLast(profile.phoneNumber.slice(7,11));
                setDetailAddress(profile.detail);
                setJob(profile.organizationType);
                setGender(profile.gender === '남자' ? 'MAN' : 'WOMAN');
                setWorkPlace(profile.organizationName);
                setSelectedSchool({schoolName: profile.organizationName, region: '', estType: ''});
                setDetailJob(profile.position);
            })
            .catch((err)=>{})
    }, []);

    /*----------------정보 수정------------------------*/
    const handleSignup = (e) => {
        if(isSchool.includes(job)){
            setWorkPlace(selectedSchool.schoolName);
        }
        e.preventDefault();

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

        apiClient.post('/api/v1/editProfile', {
            name,
            birth: changeBirth(),
            gender,
            street: postcode,
            city: sido,
            zipCode: address,
            detail: detailAddress,
            phoneNumber : prefix+middle+last,
            email,
            organizationType: job.slice(2),
            organizationName: workPlace,
            position: detailJob,
        }, )
            .then((res) => {
                if(res.data.code === 200){
                    alert('회원정보가 수정되었습니다')
                    navigate('/');
                }
            })
            .catch((err)=>{
                }
            )
    }

    const handleExit = () => {
        navigate('/');
    };

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

    /*------------------- 연락처 기능(post시 prefix, middle, last 합치기) ----------------*/
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
            <MainHeader isProfile='true'/>
            <div className="join2-content-container">
                <div className="join1-title">
                    <p className="join1-title-text">내 정보</p>
                    <div className="main-header-line" style={{width: '100%'}}></div>
                </div>
                <form onSubmit={handleSignup} className="join2-main-container">
                    <div className="join2-main-upper">
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
                                        <option value="064">064</option>
                                        <option value="063">063</option>
                                        <option value="062">062</option>
                                        <option value="061">061</option>
                                        <option value="055">055</option>
                                        <option value="054">054</option>
                                        <option value="053">053</option>
                                        <option value="052">052</option>
                                        <option value="051">051</option>
                                        <option value="044">044</option>
                                        <option value="043">043</option>
                                        <option value="042">042</option>
                                        <option value="041">041</option>
                                        <option value="033">033</option>
                                        <option value="032">032</option>
                                        <option value="031">031</option>
                                        <option value="02">02</option>
                                        <option value="010">010</option>
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
                                        <option value="초등학생">초등학생</option>
                                        <option value="중학생">중학생</option>
                                        <option value="고등학생">고등학생</option>
                                        <option value="대학생">대학생</option>
                                        <option value="컴퓨터/인터넷">컴퓨터/인터넷</option>
                                        <option value="언론">언론</option>
                                        <option value="공무원">공무원</option>
                                        <option value="군인">군인</option>
                                        <option value="서비스업">서비스업</option>
                                        <option value="예술">예술</option>
                                        <option value="기타">기타</option>

                                    </select>
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left">
                                {!job && <p className="join2-left-text">* 학교(소속)</p>}
                                {isSchool.includes(job) && <p className="join2-left-text">* 학교</p>}
                                {job && !isSchool.includes(job) && <p className="join2-left-text">소속</p>}
                            </div>
                            <div className="join2-main-border-right">
                                <div className="join2-right-row">
                                    {!job && <input
                                        className="join2-id-input"
                                        readOnly
                                        value={" ---"}
                                        required/>
                                    }
                                    {isSchool.includes(job) &&
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
                                    {job && !isSchool.includes(job) &&
                                        <input
                                            className="join2-id-input"
                                            value={workPlace}
                                            onChange={(e) => {
                                                setWorkPlace(e.target.value)
                                            }}>
                                        </input>}
                                </div>
                            </div>
                        </div>
                        <div className="join2-main-border">
                            <div className="join2-main-border-left" style={{borderBottom: 'none'}}>
                                {!job && <p className="join2-left-text">* 학년(부서)</p>}
                                {isSchool.includes(job) && <p className="join2-left-text">* 학년</p>}
                                {job && !isSchool.includes(job) && <p className="join2-left-text">부서</p>}
                            </div>
                            <div className="join2-main-border-right" style={{borderBottom: 'none'}}>
                                <div className="join2-right-row">
                                    {job && isSchool.includes(job) ? (
                                        <select className="join2-id-input" value={detailJob}
                                                onChange={(e) => {
                                                    setDetailJob(e.target.value)
                                                }} required>
                                            <option value="">---</option>
                                            {job.startsWith('초등학생') && (
                                                <>
                                                    {[1, 2, 3, 4, 5, 6].map((grade) => (
                                                        <option key={grade} value={grade}>
                                                            {grade}학년
                                                        </option>
                                                    ))}
                                                </>
                                            )}
                                            {(job.startsWith('중학생') || job.startsWith('고등학생')) && (
                                                <>
                                                    {[1, 2, 3].map((grade) => (
                                                        <option key={grade} value={grade}>
                                                            {grade}학년
                                                        </option>
                                                    ))}
                                                </>
                                            )}
                                            {job.startsWith('대학생') && (
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
                                    ) : job && !isSchool.includes(job) ? (
                                        <input
                                            type="text"
                                            className="join2-id-input"
                                            value={detailJob}
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
                        <button className="join1-button" type="submit">수정하기</button>
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

export default MyProfile;