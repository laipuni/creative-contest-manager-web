import React from "react";
import { Link, useLocation } from "react-router-dom";
import "./sidebar.css";

const Sidebar = () => {
    const location = useLocation();

    return (
        <div className="sidebar-container">
            <div className="sidebar-header">
                <p className="sidebar-main-title">경진대회</p>
                <p className="sidebar-sub-title">CREATIVE PROBLEM<br />SOLVING FESTIVAL</p>
            </div>

            <div className="sidebar-section">
                <div className="sidebar-category">대회 정보</div>
                <div className="sidebar-sub-content">
                    <Link to="/test/info" className={`sidebar-text ${location.pathname === "/test/info" ? "active" : ""}`}>대회참가 안내</Link>
                    <Link to="/register/info" className={`sidebar-text ${location.pathname.startsWith("/register") ? "active" : ""}`}>대회참가 접수</Link>
                </div>
            </div>

            <div className="sidebar-section">
                <div className="sidebar-category">문제 풀기</div>
                <div className="sidebar-sub-content">
                    <Link to="/test/realTest/info" className={`sidebar-text ${location.pathname.startsWith("/test/realTest") ? "active" : ""}`}>예선문제 풀기</Link>
                    <div
                        style={{cursor: 'pointer'}}
                        onClick={()=>alert('준비 중입니다')}
                        className={`sidebar-text ${location.pathname === "/test/pastTest" ? "active" : ""}`}>연습문제 풀기</div>
                </div>
            </div>

            <div className="sidebar-section">
                <div className="sidebar-category">커뮤니티</div>
                <div className="sidebar-sub-content">
                    <Link to="/community/notice" className={`sidebar-text ${location.pathname.startsWith("/community/notice") ? "active" : ""}`}>공지사항</Link>
                </div>
            </div>

            <div className="sidebar-section">
                <div className="sidebar-category">지원</div>
                <div className="sidebar-sub-content">
                    <Link to="/certificate/info" className={`sidebar-text ${location.pathname === "/certificate/info" ? "active" : ""}`}>증명서 발급</Link>
                    <Link to="/qna" className={`sidebar-text ${location.pathname === "/qna" ? "active" : ""}`}>QnA</Link>
                </div>
            </div>
        </div>
    );
};

export default Sidebar;
