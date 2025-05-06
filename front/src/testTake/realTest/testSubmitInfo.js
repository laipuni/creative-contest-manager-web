import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import rocket from "../../styles/images/solve_icon.png"
import {Link} from "react-router-dom";
import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

const TestSubmitInfo = () => {
    const [registerInfo, setRegisterInfo] = useState([]);

    //문제 제출내역 가져오기
    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res)=>{
                if(res.data.data){
                    apiClient.get(`/api/contests/${res.data.data.contestId}/team-solves`, {skipErrorHandler: true})
                        .then((res) => {
                            setRegisterInfo(res.data.data)
                        })
                        .catch((err)=>{
                            if(err.response.status !== 400) alert(err.response.data.message);
                        })
                }
            })
            .catch((err)=>{})
    }, []);

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
                                {registerInfo.length > 0 && <div className="registerInfo-bot-content">
                                    <p className="registerInfo-bot-text">{registerInfo.teamName}</p>
                                    <p className="registerInfo-bot-text">{registerInfo.updatedAt}</p>
                                    <p className="registerInfo-bot-text">{registerInfo.modifyCount}</p>
                                </div>}
                                <div className="registerInfo-bot-buttonbox">
                                    <Link to="/test/realTest/submit" className="registerInfo-bot-button">
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