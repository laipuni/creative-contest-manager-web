import React, {useState} from 'react'
import '../member/memberLogin.css'
import "../../../styles/styles.css"
import SubHeader from "../../../components/subHeader/subHeader";
import locker from "../../../styles/images/locker.png"
import apiClient from "../../../templates/apiClient";
const AdminLogin = () => {
    const [userId, setUserId] = useState('')
    const [password, setPassword] = useState('')
    function handleSignup() {
        /*----------rest api------------
        apiClient.post('/api/auth/login', {username: userId, password})
         */
    }
    return (
        <div className="login-page-container">
            <SubHeader />
            <form onSubmit={handleSignup} className="login-content-container">
                <div className="login-content-image">
                    <img src={locker} className="locker-image"></img>
                </div>
                <div className="login-content-text">
                    <p className="login-title">관리자 로그인</p>
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
                            <button type="submit" className="login-button">로그인</button>
                        </div>
                    </div>
                    <div className="login-underline"></div>
                </div>
            </form>
        </div>
    );
}

export default AdminLogin;