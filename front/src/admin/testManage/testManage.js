import React from 'react'
import './testManage.css'
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";

const TestManage = () => {
    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar/>
            </div>
        </div>
    )
}

export default TestManage;