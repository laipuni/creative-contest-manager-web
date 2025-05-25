import React, {useEffect, useState} from 'react';
import './mainHeader.css'
import '../../styles/styles.css'
import logo from '../../styles/images/trophy.png'
import { Link } from 'react-router-dom'
import apiClient from "../../templates/apiClient";
import axios from "axios";
import {FaUser} from "react-icons/fa";
const MainHeader = ({underbarWidth = "75%", isProfile = 'false'}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem("isAuthenticated"));
    const [isAdmin, setIsAdmin] = useState(localStorage.getItem("isAdmin"));
    const [userName, setUserName] = useState(null);
    useEffect(() => {
        if(isAuthenticated || isAdmin) {
            apiClient.get('/api/members/user-info', {skipErrorHandler: true})
                .then((res) => {
                    setUserName(res.data.data.name);
                })
                .catch((err) => {
                    if(err.response.status === 401) {
                        localStorage.removeItem("isAuthenticated");
                        localStorage.removeItem("isAdmin");
                        setIsAuthenticated(false);
                        setIsAdmin(false);
                    }
                    else{
                        alert(err.response.data.message);
                    }
                });
        }
    }, []);


    function handleLogout() {
        apiClient.post('/api/auth/logout')
            .then((res)=>{
                if(isAuthenticated === 'true') {
                    localStorage.removeItem("isAuthenticated");
                    setIsAuthenticated('false');
                }
                else{
                    localStorage.removeItem("isAdmin");
                    setIsAdmin('false');
                }
                setUserName(null);
                window.location.reload();
                //csrf 토큰 다시 얻어오기
                axios.get("/api/csrf")
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
                    {(isAuthenticated === 'true' || isAdmin === 'true') &&
                        <div className="main-header-user-welcome">
                            <FaUser/>
                            <span>{userName}님 환영합니다</span>
                        </div>
                    }
                    {isAuthenticated !== 'true' && isAdmin !== 'true' &&
                        <div className="main-header-menu" style={{justifyContent: 'flex-end', padding: '5px'}}>
                            <div className="main-header-menu-item">
                                <Link to="/" className="main-header-menu-item-text">HOME</Link>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <Link to="/member/login" className="main-header-menu-item-text">LOGIN</Link>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <Link to="/join/policy" className="main-header-menu-item-text">JOIN</Link>
                                {isAdmin === 'true' && <div className="main-header-menu-item-line"></div>}
                            </div>
                        </div>}
                    {(isAuthenticated === 'true' || isAdmin === 'true') &&
                        <>
                            <div className="main-header-menu-item">
                                <Link to="/" className="main-header-menu-item-text">HOME</Link>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <p onClick={handleLogout} style={{cursor: 'pointer'}}
                                   className="main-header-menu-item-text">LOGOUT</p>
                                <div className="main-header-menu-item-line"></div>
                            </div>
                            <div className="main-header-menu-item">
                                <Link to="/member/profile" className="main-header-menu-item-text">MYPAGE</Link>
                                {isAdmin === 'true' && <div className="main-header-menu-item-line"></div>}
                            </div>
                            {isAdmin === 'true' &&
                                <div className="main-header-menu-item">
                                    <Link to="/admin/memberList" className="main-header-menu-item-text">ADMIN</Link>
                                </div>
                            }
                        </>}
                </div>
            </div>
            {isProfile === 'false' && <>
                <div className="main-header-bot">
                    <Link to="/" className="main-header-logo">
                        <img src={logo} alt="logo" className="main-logo-image"/>
                        <p className="main-logo-text">Creative Problem<br/>Solving Festival</p>
                    </Link>
                    <div className="main-header-bot-right">
                        <div className="main-header-bot-textbox">
                            <Link to="/test/info" className="main-header-bot-text">대회안내</Link>
                            <span className="main-header-bot-circle"></span>
                            <Link to="/test/realTest/info" className="main-header-bot-text">문제풀이</Link>
                            <span className="main-header-bot-circle"></span>
                            <Link to="/qna" className="main-header-bot-text">QnA</Link>
                            <span className="main-header-bot-circle"></span>
                            <Link to="/community/notice" className="main-header-bot-text">커뮤니티</Link>
                        </div>
                    </div>
                </div>
            <div className="main-header-line" style={{width: underbarWidth}}></div>
            </>
            }
        </div>
    )
}

export default MainHeader;