import React from 'react';
import './adminSidebar.css'
import {Link, useLocation} from "react-router-dom";

const AdminSidebar = ({height}) => {
    const location = useLocation();
    //TODO- 데이터 분석 페이지 링크 연동
    return (
        <div className="adminSidebar-container" style={{height: height}}>
            <Link to="/admin/teamList"
                  className={`adminSidebar-category ${location.pathname === "/admin/teamList" ? "active" : ""}`}>
                <p className="adminSidebar-text">팀 목록</p>
            </Link>
            <Link to="/admin/testManage"
                  className={`adminSidebar-category ${location.pathname === "/admin/testManage" ? "active" : ""}`}>
                <p className="adminSidebar-text">예선 관리</p>
            </Link>
            <Link to="/admin/certificates"
                  className={`adminSidebar-category ${location.pathname === "/admin/certificates" ? "active" : ""}`}>
                <p className="adminSidebar-text">증명서 관리</p>
            </Link>
            <div className="adminSidebar-category">
                <p className="adminSidebar-text">데이터 분석</p>
            </div>
        </div>
    )
}

export default AdminSidebar;