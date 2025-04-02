import React from 'react';
import './mainHeader.css'
import '../../styles/styles.css'
import logo from '../../styles/images/trophy.png'
import { Link } from 'react-router-dom'
const MainHeader = () => {
    return (
        <div className="main-header-container">
            <div className="main-header-top">
                <div className="main-header-menu">
                    <div className="main-header-menu-item">
                        <Link to="/" className="main-header-menu-item-text">HOME</Link>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <Link to="/member-login" className="main-header-menu-item-text">LOGIN</Link>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <Link to="/join" className="main-header-menu-item-text">JOIN</Link>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <Link to="/admin-login" className="main-header-menu-item-text">ADMIN</Link>
                    </div>
                </div>
            </div>
            <div className="main-header-bot">
                <Link to="/" className="main-header-logo">
                    <img src={logo} alt="logo" className="main-logo-image"/>
                    <p className="main-logo-text">Creative Problem<br />Solving Festival</p>
                </Link>
                <div className="main-header-bot-right">
                    <div className="main-header-bot-textbox">
                        <p className="main-header-bot-text">시험안내</p>
                        <span className="main-header-bot-circle"></span>
                        <p className="main-header-bot-text">문제풀이</p>
                        <span className="main-header-bot-circle"></span>
                        <p className="main-header-bot-text">QnA</p>
                    </div>
                </div>
            </div>
            <div className="main-header-line"></div>
        </div>
    )
}

export default MainHeader;