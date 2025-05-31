import React, {useEffect} from 'react';
import MainHeader from "../components/mainHeader/mainHeader";
import "./main.css"
import '../styles/styles.css'
import team from "../styles/images/main_team.png"
import test from "../styles/images/main_real_test.png"
import info from "../styles/images/main_profile_certificate.png"
import practice from "../styles/images/main_practice_test.png"
import big_logo from "../styles/images/main_big_picture.png"
import {Link} from "react-router-dom";
import MainBento from "./mainBento";
const Main = () => {

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);
    return (
        <div className="main-page-container">
            <div className="main-content-container">
                <MainHeader />
                <div className="main-bento-container">
                    <MainBento />
                </div>
                    {/*<div className="main-content-bento">
                        <div className="bento-top">
                            <Link to="/register/info" className="bento-top-content">
                                <div className="bento-content-textbox">
                                    <p className="bento-content-title">
                                        대회참가&nbsp;<span style={{color: 'rgba(255, 13, 13, 0.81)'}}>접수</span>
                                    </p>
                                    <p className="bento-content-subtitle">
                                        팀장을 통해 팀원의 정보를 입력하고<br />
                                        <span style={{color: '#FF0202'}}>새로운 팀</span>을 등록해요
                                    </p>
                                </div>
                                <div className="bento-image-container">
                                    <img src={team} alt="logo" className="bento-image"/>
                                </div>
                            </Link>
                            <Link to="/test/realTest/info" className="bento-top-content" style={{background: "rgba(89, 167, 255, 0.36)"}}>
                                <div className="bento-content-textbox">
                                    <p className="bento-content-title">
                                        <span style={{color: '#3D2FFC'}}>예선문제</span>&nbsp;풀기
                                    </p>
                                    <p className="bento-content-subtitle">
                                        팀원과 함께&nbsp;<span style={{color: '#3D2FFC'}}>문제를 풀고</span><br/>
                                        기간 내에&nbsp;<span style={{color: '#3D2FFC'}}>제출</span>해요
                                    </p>
                                </div>
                                <div className="bento-image-container">
                                    <img src={test} alt="test" className="bento-image"/>
                                </div>
                            </Link>
                        </div>
                        <div className="bento-bot">
                            <div className="bento-bot-left">
                                <Link to="/certificate/info" className="bento-bot-left-upper" style={{textDecoration: 'none'}}>
                                    <div className="bento-content-textbox" style={{width: '50%'}}>
                                        <p className="bento-content-title" style={{fontSize: 32}}>
                                            <span style={{color: '#2B72F5'}}>증명서</span>&nbsp;발급
                                        </p>
                                        <p className="bento-content-subtitle">
                                            예선 참가/합격<br/>
                                            <span style={{color: '#2B72F5'}}>증명서</span>를 발급해요
                                        </p>
                                    </div>
                                    <div className="bento-image-container">
                                        <img src={info} alt="info" className="bento-image"/>
                                    </div>
                                </Link>
                                <Link to='/test/pastTest' className="bento-bot-left-lower">
                                    <div className="bento-content-textbox" style={{width: '50%'}}>
                                        <p className="bento-content-title" style={{fontSize: 32}}>
                                            <span style={{color: '#3D2FFC', fontSize: 32}}>연습문제</span>&nbsp;풀기
                                        </p>
                                        <p className="bento-content-subtitle">
                                            실전에 대비해<br/>
                                            <span style={{color: '#3D2FFC'}}>연습문제</span>를 풀어봐요
                                        </p>
                                    </div>
                                    <div className="bento-image-container">
                                        <img src={practice} alt="practice" className="bento-image" style={{height: '50%'}}/>
                                    </div>
                                </Link>
                            </div>
                            <div className="bento-bot-right">
                                <img src={big_logo} alt="big_logo" className="main-image"/>
                            </div>
                        </div>
                    </div>*/}
            </div>
        </div>
    );
};

export default Main;