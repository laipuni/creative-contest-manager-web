import React from 'react'
import logo from "../../../styles/images/trophy.png";
import "./adminHeader.css"
import {Link, useNavigate} from "react-router-dom";

const AdminHeader = () => {
    const navigate = useNavigate();

    function handleLogout() {
        //TODO - 관리자 로그아웃 rest api 연동
        localStorage.removeItem('isAdmin');
        navigate('/');
    }
    return (
        <div className="adminHeader-container">
            <Link to="/" className="adminHeader-logo-container" style={{textDecoration: 'none'}}>
                <img src={logo} alt="logo" className="adminHeader-logo-image"/>
                <p className="adminHeader-logo-text">Creative Problem<br/>Solving Festival</p>
            </Link>
            <div onClick={handleLogout} className="adminHeader-logout-button">
                <p className="adminHeader-logout-text">LOGOUT</p>
            </div>
        </div>
    )
}

export default AdminHeader;