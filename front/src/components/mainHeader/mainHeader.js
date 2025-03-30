import React from 'react';
import './mainHeader.css'
import '../../styles/styles.css'
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
                        <p className="main-header-menu-item-text">LOGIN</p>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <p className="main-header-menu-item-text">JOIN</p>
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <p className="main-header-menu-item-text">ADMIN</p>
                    </div>
                </div>
            </div>
            <div className="main-header-bot"></div>
            <div className="main-header-line"></div>
        </div>
    )
}

export default MainHeader;