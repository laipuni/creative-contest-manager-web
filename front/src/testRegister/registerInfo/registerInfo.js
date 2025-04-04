import React from 'react';
import './registerInfo.css';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import {Link} from "react-router-dom";

const registerInfo = () => {
    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"예선시험 접수"} imgSrc={trophyLogo}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">접수 내역</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="registerInfo-body-bot">
                                <div className="registerInfo-bot-title">
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">접수번호</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">접수일자</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">팀장(이메일)</p>
                                </div>
                                <div className="registerInfo-bot-content">
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <p className="registerInfo-bot-text">팀명</p>

                                </div>
                                <div className="registerInfo-bot-buttonbox">
                                    <Link to="/" className="registerInfo-bot-button">접수하기</Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )};


export default registerInfo;