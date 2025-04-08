import React, {useState} from 'react'
import './memberLogin.css'
import "../../styles/styles.css"
import SubHeader from "../../components/subHeader/subHeader";
import locker from "../../styles/images/locker.png"
import {Link, useNavigate} from "react-router-dom";
import apiClient from "../../templates/apiClient";
import FindIdModal from "../../components/modals/findIdModal";
import FindPwModal from "../../components/modals/findPwModal";
const MemberLogin = () => {
    const [userId, setUserId] = useState('')
    const [password, setPassword] = useState('')
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isPwModalOpen, setIsPwModalOpen] = useState(false);
    const navigate = useNavigate();

    const handleSignup = (e) => {
        e.preventDefault();
        /*----------rest api------------*/
        apiClient.post('/api/auth/login', {username: userId, password})
            .then((res)=>{
                navigate('/');
            })
            .catch((err)=>{})
    }

    function handleClose() {
        setIsModalOpen(false);
    }


    return (
        <div className="login-page-container">
            <SubHeader />
            <form onSubmit={handleSignup} className="login-content-container">
                <div className="login-content-image">
                    <img src={locker} className="locker-image"></img>
                </div>
                <div className="login-content-text">
                <p className="login-title">회원 로그인</p>
                    <div className="login-body">
                        <div className="login-input-field">
                            <p className="login-input-title">아이디</p>
                            <input className="login-input-body"
                                   type="text"
                                   value={userId}
                                   onChange={e => setUserId(e.target.value)}
                                   required
                                   />
                        </div>
                        <div className="login-input-field">
                            <p className="login-input-title">비밀번호</p>
                            <input className="login-input-body"
                                   type="password"
                                   value={password}
                                   onChange={e => setPassword(e.target.value)}
                                   required
                            />
                            <div className="login-forgotpw">
                                <button type="button"
                                        onClick={()=>{setIsModalOpen(true)}}
                                        className="login-forgotpw-text">아이디 찾기</button>
                                {isModalOpen && <FindIdModal onClose={handleClose} /> }
                                <p className="login-forgotpw-text" style={{color: 'black'}}>/</p>
                                <button type="button"
                                        onClick={() => {setIsPwModalOpen(true)}}
                                        className="login-forgotpw-text">비밀번호 찾기</button>
                                {isPwModalOpen && <FindPwModal onClose={()=>{setIsPwModalOpen(false)}}/>}
                            </div>
                            <button type="submit" className="login-button">로그인</button>
                        </div>
                    </div>
                    <div className="login-underline"></div>
                    <div className="login-joinmessage">
                        <p className="login-joinmessage-text">
                            아직 회원가입을 안하셨나요?</p>
                        <Link to="/join/policy" className="login-joinmessage-hyper" style={{color: 'blue'}}>
                            회원가입
                        </Link>
                    </div>
                </div>
            </form>
        </div>
    );
}

export default MemberLogin;