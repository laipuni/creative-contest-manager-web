import React from 'react';
import './adminSidebar.css'
import {Link, useLocation} from "react-router-dom";

const AdminSidebar = ({height}) => {
    const location = useLocation();
    //TODO- 데이터 분석 페이지 링크 연동
    return (
        <div className="adminSidebar-container" style={{height: height}}>
            <Link to="/admin/memberList"
                  className={`adminSidebar-category ${location.pathname === "/admin/memberList" ? "active" : ""}`}>
                <p className="adminSidebar-text">회원 목록</p>
            </Link>
            <Link to="/admin/teamList"
                  className={`adminSidebar-category ${location.pathname === "/admin/teamList" ? "active" : ""}`}>
                <p className="adminSidebar-text">팀 목록</p>
            </Link>
            <Link to="/admin/testManage"
                  className={`adminSidebar-category ${location.pathname === "/admin/testManage" ? "active" : ""}`}>
                <p className="adminSidebar-text">대회 관리</p>
            </Link>
            <Link to="/admin/certificates"
                  className={`adminSidebar-category ${location.pathname === "/admin/certificates" ? "active" : ""}`}>
                <p className="adminSidebar-text">증명서 관리</p>
            </Link>
            <Link to="/admin/notices" className={`adminSidebar-category ${location.pathname.startsWith("/admin/notices") ? "active" : ""}`} >
                <p className="adminSidebar-text">공지사항</p>
            </Link>
            <Link to="/admin/quizAI" className={`adminSidebar-category ${location.pathname === "/admin/quizAI" ? "active" : ""}`} >
                <p className="adminSidebar-text">AI 문제생성</p>
            </Link>
            <Link to="/admin/dataManage" className={`adminSidebar-category ${location.pathname === "/admin/dataManage" ? "active" : ""}`}>
                <p className="adminSidebar-text">데이터 분석</p>
            </Link>
        </div>
    )
}

export default AdminSidebar;