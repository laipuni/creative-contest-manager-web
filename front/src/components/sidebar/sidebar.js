import React from 'react'
import './sidebar.css'
import '../../styles/styles.css'
import {Link, useLocation} from 'react-router-dom'

const Sidebar = () => {
    const location = useLocation()

    return (
        <div className="sidebar-container">
            <p className="sidebar-title">커뮤니티</p>
            <div className="sidebar-content">
                <Link to="/community/notice"
                      className={`sidebar-text ${location.pathname === "/community/notice" ? "active" : ""}`}>공지사항</Link>
            </div>
            <div className="sidebar-underline"></div>
            <p className="sidebar-title">시험 정보</p>
            <div className="sidebar-content">
                <Link to="/test/info" className={`sidebar-text ${location.pathname === "/test/info" ? "active" : ""}`}>
                    대회참가 안내
                </Link>
                <Link to="/register/info" className={`sidebar-text 
                ${location.pathname.startsWith("/register") ? "active" : ""}`}>대회참가 접수</Link>
            </div>
            <div className="sidebar-underline"></div>
            <p className="sidebar-title">문제 풀기</p>
            <div className="sidebar-content">
                <Link to="/test/realTest/info"
                      className={`sidebar-text ${location.pathname.startsWith("/test/realTest") ? "active" : ""}`}>
                    예선문제 풀기</Link>
                <Link to="/test/pastTest"
                      className={`sidebar-text ${location.pathname === "/test/pastTest" ? "active" : ""}`}>연습문제
                    풀기</Link>
            </div>
            <div className="sidebar-underline"></div>
            <p className="sidebar-title">지원</p>
            <div className="sidebar-content">
                <Link to="/certificate/info"
                      className={`sidebar-text ${location.pathname === "/certificate/info" ? "active" : ""}`}>증명서
                    발급</Link>
                <Link to="/qna" className={`sidebar-text ${location.pathname === "/qna" ? "active" : ""}`}>QnA</Link>
            </div>

        </div>
    )
}

export default Sidebar
