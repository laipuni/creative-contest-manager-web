import './testInfo.css';
import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import {format} from 'date-fns';
import apiClient from "../../templates/apiClient";

const TestInfo = () => {
    const [registerStartDate, setRegisterStartDate] = useState('');
    const [registerEndDate, setRegisterEndDate] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    //시험 일정 가정
    useEffect(() => {
        apiClient.get('/api/contests/latest')
          .then((res) => {
              if(res.data.data){
                  const registerStart = new Date(res.data.data.registrationStartAt);
                  const registerEnd = new Date(res.data.data.registrationEndAt);
                  const start = new Date(res.data.data.startTime);
                  const end = new Date(res.data.data.endTime);
                  setStartDate(format(start, 'yyyy.MM.dd HH:mm'));
                  setEndDate(format(end, 'yyyy.MM.dd HH:mm'));
                  setRegisterStartDate(format(registerStart, 'yyyy.MM.dd HH:mm'));
                  setRegisterEndDate(format(registerEnd, 'yyyy.MM.dd HH:mm'));
              }
          });
    }, [])


    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
            <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar />
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={`대회참가 안내`} imgSrc={trophyLogo}/>
                        <div className="testInfo-text-container">
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">접수 기간</p>
                                    <div className="testInfo-line"></div>
                                    {registerStartDate && registerEndDate && <p className="testInfo-description"
                                                                style={{
                                                                    color: '#FFA220',
                                                                    fontSize: '25px',
                                                                    fontWeight: '700'
                                                                }}>{registerStartDate} &nbsp;~&nbsp; {registerEndDate}</p>}
                                    {!registerStartDate && <p className="testInfo-description"
                                                      style={{color: '#FFA220', fontSize: '25px', fontWeight: '700'}}>추후
                                        공지예정</p>}
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">대회 기간</p>
                                    <div className="testInfo-line"></div>
                                    {startDate && endDate && <p className="testInfo-description"
                                                                style={{
                                                                    color: '#FFA220',
                                                                    fontSize: '25px',
                                                                    fontWeight: '700'
                                                                }}>{startDate} &nbsp;~&nbsp; {endDate}</p>}
                                    {!startDate && <p className="testInfo-description"
                                                      style={{color: '#FFA220', fontSize: '25px', fontWeight: '700'}}>추후
                                        공지예정</p>}
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">진행방법</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                        <span className="testInfo-description"
                                              style={{
                                                  color: '#2081FF',
                                                  fontSize: '20px',
                                                  fontWeight: '700'
                                              }}>온라인 </span>
                                        진행 / 팀원이 함께 문제 해결 후 팀장을 통해 답안 제출</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">문제유형</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                        공통 문제 1문제와 초/중등, 고등/일반 각 수준에 맞추어
                                        1문제를 더해 총 2문제 출제</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">유의사항</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                        • 참가팀은 예선기간 내 대회 웹사이트를 통해 예선대회 참가<br/>
                                        • 답안 작성은 팀장만 가능하며 팀원은 문제보기만 가능<br/>
                                        • 답안 제출 시 문제 풀이 과정도 별도로 기술<br/>
                                        • 답안 제출 시 pdf 파일과 텍스트 활용 가능<br/>
                                        • 답안은 예선기간 내 횟수 제한없이 수정 제출 가능<br/>
                                        • 동점 시 제출시간, 풀이 과정을 종합 평가하여 선정</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">채점방법</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                        풀이 과정 및 창의성 평가</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">팀 구성</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                        같은 부문의 2~3명이 1개팀으로 구성</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                            <div className="testInfo-text-inner-container">
                                <div className="testInfo-textbox">
                                    <p className="testInfo-title">본선 진출팀</p>
                                    <div className="testInfo-line"></div>
                                    <p className="testInfo-description">
                                         <span className="testInfo-description"
                                               style={{
                                                   color: '#2081FF',
                                                   fontSize: '20px',
                                                   fontWeight: '700'
                                               }}>총 100팀</span> 선발<br/>
                                        (초등부 25팀/중등부 25팀/고등부 25팀/대학일반부 25팀)</p>
                                </div>
                                <div className="testInfo-underline"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TestInfo;