import React, {useEffect, useState} from 'react';
import './mainHeader.css'
import '../../styles/styles.css'
import logo from '../../styles/images/trophy.png'
import { Link } from 'react-router-dom'
import apiClient from "../../templates/apiClient";
import axios from "axios";
const MainHeader = ({underbarWidth = "75%"}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem("isAuthenticated"));
    function handleLogout() {
        apiClient.post('/api/auth/logout')
            .then((res)=>{
                localStorage.removeItem("isAuthenticated");
                setIsAuthenticated(null);
                window.location.reload();
                //csrf 토큰 다시 얻어오기
                axios.get("http://back:8080/api/csrf")
                    .then((data) => {
                    })
                    .catch((error) => {alert(error.message)});
            })
            .catch((err)=>{})
    }

    return (
        <div className="main-header-container">
            <div className="main-header-top">
                <div className="main-header-menu">
                    <div className="main-header-menu-item">
                        <Link to="/" className="main-header-menu-item-text">HOME</Link>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    {isAuthenticated !== 'true' &&
                        <>
                            <div className="main-header-menu-item">
                                <Link to="/member/login" className="main-header-menu-item-text">LOGIN</Link>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                            <Link to="/join/policy" className="main-header-menu-item-text">JOIN</Link>
                            <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                            <Link to="/admin/login" className="main-header-menu-item-text">ADMIN</Link>
                            </div>
                        </>}
                    {isAuthenticated === 'true' &&
                        <>
                            <div className="main-header-menu-item">
                                <p onClick={handleLogout} style={{cursor: 'pointer'}}
                                   className="main-header-menu-item-text">LOGOUT</p>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <Link to="/member/profile" className="main-header-menu-item-text">MYPAGE</Link>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <Link to="/admin/login" className="main-header-menu-item-text">ADMIN</Link>
                            </div>
                        </>}
                </div>
            </div>
            <div className="main-header-bot">
                <Link to="/" className="main-header-logo">
                    <img src={logo} alt="logo" className="main-logo-image"/>
                    <p className="main-logo-text">Creative Problem<br/>Solving Festival</p>
                </Link>
                <div className="main-header-bot-right">
                    <div className="main-header-bot-textbox">
                        <Link to="/test/info" className="main-header-bot-text">시험안내</Link>
                        <span className="main-header-bot-circle"></span>
                        <Link to="/test/realTest/info" className="main-header-bot-text">문제풀이</Link>
                        <span className="main-header-bot-circle"></span>
                        <Link to="/qna" className="main-header-bot-text">QnA</Link>
                    </div>
                </div>
            </div>
            <div className="main-header-line" style={{width: underbarWidth}}></div>
        </div>
    )
}

export default MainHeader;