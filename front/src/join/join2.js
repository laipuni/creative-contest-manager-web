import React, {useState} from 'react';
import './join2.css'
import SubHeader from "../components/subHeader/subHeader";
import {useNavigate} from "react-router-dom";

const Join2 = () => {
    const [userId, setUserId] = useState('');
    const [isDuplicate, setIsDuplicate] = useState(true);
    const [idErrorMessage, setIdErrorMessage] = useState('');
    const exampleIds = ['user1', 'user2'];
    const navigate = useNavigate();

    /*------------------- 회원가입 & 나가기 버튼 기능----------------*/
    const handleNextPage = () => {
            navigate('/join1');
    };

    const handleExit = () => {
        navigate('/');
    };

    /*------------------- 아이디 중복검사 기능 ----------------*/
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
                <div className="join2-main-container">
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
                                    {idErrorMessage && <p className="error-message"
                                                          style={{color: 'red'}}>{idErrorMessage}</p>}
                                    {!isDuplicate && !idErrorMessage &&
                                        <p className="error-message" style={{color: 'green'}}>사용 가능한 아이디입니다.</p>}
                                </div>
                                <p className="join2-id-info">
                                    회원ID는 가입 후 바꾸실 수 없으니 신중하게 결정 해 주세요.
                                    <br /><span style={{color: '#2489DC'}}> 영문자로 시작하는 4~12자의 영문과 숫자를 사용하시고
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
                                        value={userId}
                                        onChange={handleIdChange}
                                    />
                                    <p className="error-message">영문자, 숫자 조합 4~8자 이하를 입력하세요.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="join1-buttonbox-lower">
                        <button className="join1-button" onClick={handleNextPage}>가입하기</button>
                        <button className="join1-button"
                                style={{background: 'lightgray', color: 'black'}}
                                onClick={handleExit}>나가기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Join2;