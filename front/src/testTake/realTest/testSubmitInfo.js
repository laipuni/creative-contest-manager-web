import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import rocket from "../../styles/images/solve_icon.png"
import {Link} from "react-router-dom";
import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

//출력 예시
const exampleData = {
    teamName: "챌린저 팀",
    registerDate: "2025-04-04T15:00:00.000Z",
    registerCnt: 3
};

const TestSubmitInfo = () => {
    const [teamName, setTeamName] = useState('');
    const [registerDate, setRegisterDate] = useState('');
    const [registerCnt, setRegisterCnt] = useState(0);

    useEffect(() => {
        setTeamName(exampleData.teamName);
        setRegisterDate(format(new Date(exampleData.registerDate), 'yyyy-MM-dd'));
        setRegisterCnt(exampleData.registerCnt);
    }, []);

    /*-----------------제출 내역 가져오기---------
    apiClient.get('/api/test/submitInfo'})
        .then((res)=>{
            setTeamName(res.data.teamName);
            setRegisterDate(format(new Date(res.data.registerDate), 'yyyy-MM-dd'));
            setRegisterCnt(res.data.registerCnt);
        });

     */

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"예선문제 풀기"} imgSrc={testLogo} imageWidth='18%'
                                      backgroundColor={'linear-gradient(90deg, #FF6200 0%, #FDEB85 100%)'}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">제출 내역</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="registerInfo-body-bot">
                                <div className="registerInfo-bot-title">
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">제출일자</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">제출횟수</p>
                                </div>
                                {teamName && <div className="registerInfo-bot-content">
                                    <p className="registerInfo-bot-text">{teamName}</p>
                                    <p className="registerInfo-bot-text">{registerDate}</p>
                                    <p className="registerInfo-bot-text">{registerCnt}</p>
                                </div>}
                                <div className="registerInfo-bot-buttonbox">
                                    <Link to="/register/team" className="registerInfo-bot-button">
                                        <img src={rocket} alt='rocket' className="submit-rocket-img"/>문제풀기</Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};


export default TestSubmitInfo;