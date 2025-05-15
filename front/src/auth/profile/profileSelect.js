import MainHeader from "../../components/mainHeader/mainHeader";
import React from "react";
import './profileSelect.css'
import {Link} from "react-router-dom";

const ProfileSelect = () => {
    return (
        <div className="join2-page-container">
            <MainHeader isProfile='true'/>
            <div className="join1-title" style={{padding: '10px'}}>
                <p className="join1-title-text">내 정보</p>
                <div className="main-header-line" style={{width: '100%'}}></div>
            </div>
            <div className="profileSelect-content-container">
                <div className="profileSelect-button-container">
                    <Link to="/member/profile/edit" className="profileSelect-btn">
                        <p className="profileSelect-text">👤 내 정보 수정</p>
                    </Link>
                    <Link to="/certificate/info" className="profileSelect-btn" style={{backgroundColor: 'rgba(95, 164, 255, 0.09)'}}>
                        <p className="profileSelect-text">📄 증명서 발급</p>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default ProfileSelect;