import MainHeader from "../../components/mainHeader/mainHeader";
import React, {useState} from "react";
import './passwordAuth.css'
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";
const PasswordAuth = () => {
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const handleVerifyPassword = () => {
        apiClient.post('/api/members/profile/password-verify', {password})
            .then((res)=>{
                const session = res.data.data.session;
                navigate('/member/profile/edit', {state: { session }});
            })
            .catch((err)=>{})
    }
    return (
        <div className="join2-page-container">
            <MainHeader isProfile='true'/>
            <div className="join1-title" style={{padding: '10px'}}>
                <p className="join1-title-text">비밀번호 확인</p>
                <div className="main-header-line" style={{width: '100%'}}></div>
            </div>
            <div className="passwordAuth-content-container">
                <div className="passwordAuth-main-container">
                    <p className="passwordAuth-main-title">비밀번호</p>
                    <div className="passwordAuth-sub-container">
                        <div className="passwordAuth-input-container">
                            <input
                                className="passwordAuth-input"
                                value={password}
                                type="password"
                                onChange={(e)=>setPassword(e.target.value)}
                                onKeyPress={(e)=>{if(e.key === 'Enter') handleVerifyPassword()}}
                            ></input>
                            <button
                                onClick={handleVerifyPassword}
                                className="passwordAuth-button-container">
                                <p className="passwordAuth-button-text">확인</p>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default PasswordAuth;