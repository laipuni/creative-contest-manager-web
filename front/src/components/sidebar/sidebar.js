import React from 'react'
import './sidebar.css'
import '../../styles/styles.css'
import {Link, useLocation} from 'react-router-dom'

const Sidebar = () => {
    const location = useLocation()

    return (
        <div className="sidebar-container">
            <p className="sidebar-title">시험 정보</p>
            <div className="sidebar-content">
                <Link to="/test/info" className={`sidebar-text ${location.pathname === "/test/info" ? "active" : ""}`}>
                    예선시험 안내
                </Link>
                <Link to="/register/info" className={`sidebar-text 
                ${location.pathname === "/register/info" ? "active" : ""}`}>예선시험 접수</Link>
            </div>
            <div className="sidebar-underline"></div>
            <p className="sidebar-title">문제 풀기</p>
            <div className="sidebar-content">
                <Link to = "/" className="sidebar-text">예선문제 풀기</Link>
                <Link t0 = "/" className="sidebar-text">기출문제 풀기</Link>
            </div>
            <div className="sidebar-underline"></div>
            <p className="sidebar-title">지원</p>
            <div className="sidebar-content">
                <Link to = "/" className="sidebar-text">증명서 발급</Link>
                <Link to = "/" className="sidebar-text">QnA</Link>
            </div>

        </div>
    )
}

export default Sidebar
