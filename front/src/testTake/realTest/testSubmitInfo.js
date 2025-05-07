import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import rocket from "../../styles/images/solve_icon.png"
import {Link, useNavigate} from "react-router-dom";
import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

const TestSubmitInfo = () => {
    const [registerInfo, setRegisterInfo] = useState([]);
    const [contestInfo, setContestInfo] = useState(null);

    const navigate = useNavigate();

    const mapTeamAnswersToRegisterInfo = (teamAnswerList) => {
        const info = [
            { problemType: '공통', section: 'COMMON' },
            { problemType: '수준별', section: 'NON_COMMON' }
        ];

        return info.map(({ problemType, section }) => {
            let target;

            if (section === 'COMMON') {
                target = teamAnswerList.find(ans => ans.section === 'COMMON');
            } else {
                target = teamAnswerList.find(ans => ans.section !== 'COMMON');
            }

            return {
                problemType,
                updatedAt: target ? target.updatedAt : 'X',
                registerCount: target ? target.modifyCount + 1 : 0
            };
        });
    };

    //문제 제출내역 가져오기
    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res) => {
                if (res.data.data) {
                    const contestId = res.data.data.contestId;
                    setContestInfo(res.data.data);

                    apiClient.get(`/api/contests/${contestId}/team-solves`, { skipErrorHandler: true })
                        .then((res) => {
                            const teamAnswerList = res.data.data.teamAnswerList;

                            const mappedRegisterInfo = mapTeamAnswersToRegisterInfo(teamAnswerList);
                            setRegisterInfo(mappedRegisterInfo);
                        })
                        .catch((err) => {
                            alert(err.response?.data?.message || '팀 문제 정보 불러오기 실패');
                            setRegisterInfo([
                                { problemType: '공통', updatedAt: 'X', registerCount: 0 },
                                { problemType: '수준별', updatedAt: 'X', registerCount: 0 }
                            ]);
                        });
                }
            })
                        .catch((err)=>{
                            alert(err.response.data.message);
                        })
            .catch((err)=>{})
    }, []);

    //대회 참여 버튼
    const handleValidationContest = () => {
        apiClient.post(`/api/contests/${contestInfo.contestId}/join`)
            .then((res)=>{
                if(res.status === 200){
                    navigate('/test/realTest/submit');
                }
            })
            .catch((err)=>{});
    }

    //날짜 형태로 파싱
    const formatDate = (date) => {
        if(date !== 'X') return format(new Date(date), 'yyyy-MM-dd')
        else return date;
    }

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
                                    <p className="registerInfo-bot-text">문제유형</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">제출일자</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">제출횟수</p>
                                </div>
                                {registerInfo.length > 0 &&
                                    <>
                                        <div className="registerInfo-bot-content">
                                            <p className="registerInfo-bot-text">{registerInfo[0].problemType}</p>
                                            <p className="registerInfo-bot-text">{formatDate(registerInfo[0].updatedAt)}</p>
                                            <p className="registerInfo-bot-text">{registerInfo[0].registerCount}</p>
                                        </div>
                                        <div className="registerInfo-bot-content">
                                            <p className="registerInfo-bot-text">{registerInfo[1].problemType}</p>
                                            <p className="registerInfo-bot-text">{formatDate(registerInfo[1].updatedAt)}</p>
                                            <p className="registerInfo-bot-text">{registerInfo[1].registerCount}</p>
                                        </div>
                                    </>
                                }
                                <div className="registerInfo-bot-buttonbox">
                                    <div onClick={handleValidationContest} className="registerInfo-bot-button"
                                         style={{cursor: 'pointer'}}>
                                        <img src={rocket} alt="rocket" className="submit-rocket-img"/>
                                        문제풀기
                                    </div>
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