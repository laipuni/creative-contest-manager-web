import React, {useEffect, useState} from 'react';
import './registerInfo.css';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import {Link} from "react-router-dom";
import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";
import rocket from "../../styles/images/solve_icon.png";

//출력 예시
const exampleData = {
    teamName: "챌린저 팀",
    registerNum: "20250404-001",
    registerDate: "2025-04-04T15:00:00.000Z",
    leaderName: "김철수",
    leaderEmail: "leader@example.com"
};

const RegisterInfo = () => {
    const [teamName, setTeamName] = useState('');
    const [registerNum, setRegisterNum] = useState('');
    const [registerDate, setRegisterDate] = useState('');
    const [leaderName, setLeaderName] = useState('');
    const [leaderEmail, setLeaderEmail] = useState('');

    useEffect(() => {
        setTeamName(exampleData.teamName);
        setRegisterNum(exampleData.registerNum);
        setRegisterDate(format(new Date(exampleData.registerDate), 'yyyy-MM-dd'));
        setLeaderName(exampleData.leaderName);
        setLeaderEmail(exampleData.leaderEmail);
    }, []);

    /*-----------------접수 내역 가져오기---------
    apiClient.get('/api/register/info'})
        .then((res)=>{
            setTeamName(res.data.teamName);
            setRegisterNum(res.data.registerNum);
            setRegisterDate(format(new Date(res.data.registerDate), 'yyyy-MM-dd'))
            setLeaderName(res.data.leaderName)
            setLeaderEmail(res.data.leaderEmail)
        });

     */

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
                                {teamName && <div className="registerInfo-bot-content">
                                    <p className="registerInfo-bot-text">{teamName}</p>
                                    <p className="registerInfo-bot-text">{registerNum}</p>
                                    <p className="registerInfo-bot-text">{registerDate}</p>
                                    <p className="registerInfo-bot-text">{leaderName}({leaderEmail})</p>
                                </div>}
                                <div className="registerInfo-bot-buttonbox">
                                    <Link to="/register/team" className="registerInfo-bot-button">
                                        <img src={rocket} alt='rocket' className="submit-rocket-img"/>
                                        접수하기</Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};


export default RegisterInfo;