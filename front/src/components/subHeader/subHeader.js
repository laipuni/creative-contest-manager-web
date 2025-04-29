import React from 'react';
import '../mainHeader/mainHeader.css'
import '../../styles/styles.css'
import logo from '../../styles/images/trophy.png'
import { Link } from 'react-router-dom'
import './subHeader.css'
const SubHeader = () => {
    return (
        <div className="main-header-container" style={{height: 'fit-content'}}>
            <div className="main-header-top" style={{height: '35px'}}>
                <Link to="/" className="sub-header-logo">
                    <img src={logo} alt="logo" className="main-logo-image" style={{width: '34px', height: '30px'}}/>
                    <p className="main-logo-text"
                       style={{fontSize: 10, width:'fit-content'}}>Creative Problem<br />Solving Festival</p>
                </Link>
                <div className="main-header-menu">
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
                        <div className="main-header-menu-item-line"></div>
                    </div>
                    <div className="main-header-menu-item">
                        <Link to="/admin/login" className="main-header-menu-item-text">ADMIN</Link>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default SubHeader;