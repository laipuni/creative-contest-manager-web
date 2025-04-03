import './testInfo.css'
import React from 'react'
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";

const TestInfo = () => {
    return (
        <div className="testInfo-page-container">
            <MainHeader underbarWidth="100%"/>
            <div className="testInfo-content-container">
                <Sidebar />
            </div>
        </div>
    )
}

export default TestInfo;