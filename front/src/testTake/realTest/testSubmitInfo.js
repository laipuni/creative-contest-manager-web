import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import rocket from "../../styles/images/solve_icon.png"
import {Link, useNavigate} from "react-router-dom";
import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";
import TeamAnswerList from "../../admin/components/teamAnswerList/teamAnswerList";
import ContestAnswerList from "../../components/contestAnswerList/contestAnswerList";

const TestSubmitInfo = () => {
    const [registerInfo, setRegisterInfo] = useState([]);
    const [contestInfo, setContestInfo] = useState(null);
    const [submitCnt, setSubmitCnt] = useState(0);
    const [teamAnswerModal, setTeamAnswerModal] = useState({
        open: false,
        answers: null,
        teamName: null,
        contestId: null,
    });

    const navigate = useNavigate();

    //문제 제출내역 가져오기
    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res) => {
                if (res.data.data) {
                    const contestId = res.data.data.contestId;
                    setContestInfo(res.data.data);

                    apiClient.get(`/api/contests/${contestId}/team-solves`, { params: {submit_type: 'submitted' }, skipErrorHandler: true })
                        .then((res) => {
                            setSubmitCnt(res.data.data.finalSubmitCount);
                            setRegisterInfo(res.data.data.teamAnswerList);
                        })
                        .catch((err) => {
                            if(err.response?.data?.message === '해당 대회에 참여한 팀이 없습니다.'){
                                alert('팀 접수를 먼저 진행해주세요.')
                                navigate('/register/info');
                                return;
                            }
                            alert(err.response?.data?.message || '팀 문제 정보 불러오기 실패');
                            setRegisterInfo(
                                { updatedAt: 'X', registerCount: 0 });
                        });
                }
            })
                        .catch((err)=>{
                            alert(err.response.data.message);
                        })
            .catch((err)=>{})
    }, []);

    //대회 참여 버튼
    const handleValidationContest = (isFinal) => {
        console.log(isFinal);
        apiClient.post(`/api/contests/${contestInfo.contestId}/join`)
            .then((res)=>{
                if(res.status === 200){
                    if(!isFinal)
                        navigate('/test/realTest/submit');
                    else{
                        const confirmed = window.confirm("최종제출 시 기존 제출된 답안은 사라집니다. 제출하시겠습니까?");
                        if (!confirmed) return;
                        apiClient.post(`/api/contests/${contestInfo.contestId}/team-solves/complete`)
                            .then((res)=>{
                                alert('제출 완료!')
                                navigate('/test/realTest/info')
                            })
                            .catch((err)=>{})
                    }
                }
            })
            .catch((err)=>{});
    }

    //날짜 형태로 파싱
    const formatDate = (date) => {
        if(date !== 'X') return format(new Date(date), 'yyyy-MM-dd HH:mm')
        else return date;
    }

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={`예선문제 풀기`} imgSrc={testLogo} imageWidth='18%'
                                                      backgroundColor={'linear-gradient(90deg, #FF6200 0%, #FDEB85 100%)'}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">제출 내역</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="registerInfo-body-bot">
                                <div className="registerInfo-bot-title">
                                    <p className="registerInfo-bot-text">제출일시</p>
                                    <div className="registerInfo-bot-line" style={{width: '5%'}}></div>
                                    <p className="registerInfo-bot-text">제출횟수</p>
                                    <div className="registerInfo-bot-line" style={{width: '5%'}}></div>
                                    <p className="registerInfo-bot-text">제출된 답안</p>
                                </div>
                                {teamAnswerModal.open && teamAnswerModal.answers && (
                                    <ContestAnswerList
                                        answers={teamAnswerModal.answers}
                                        teamName={teamAnswerModal.teamName}
                                        contestId={contestInfo.contestId}
                                        onClose={() => setTeamAnswerModal({ open: false, answers: null, teamName: null, contestId: null })}
                                    />
                                )}
                                {submitCnt > 0 ? (
                                    <>
                                        <div className="registerInfo-bot-content">
                                            <p className="registerInfo-bot-text">{formatDate(registerInfo[0].updatedAt)}</p>
                                            <p className="registerInfo-bot-text">{submitCnt}</p>
                                            <p className="registerInfo-bot-text"
                                               style={{cursor: 'pointer'}}
                                               onClick={() => {
                                                   setTeamAnswerModal({ open: true, answers: registerInfo, teamName: registerInfo[0].teamName, contestId: contestInfo.contestId });
                                               }}>📄</p>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div className="registerInfo-bot-content">
                                            <p className="registerInfo-bot-text">답안 제출내역 없음</p>
                                        </div>
                                    </>
                                )}
                                <div className="registerInfo-bot-buttonbox">
                                    <div onClick={(e)=>handleValidationContest(false)} className="registerInfo-bot-button"
                                         style={{cursor: 'pointer', padding: '27px 5px'}}>
                                        문제풀기
                                    </div>
                                    <div onClick={(e)=>handleValidationContest(true)} className="registerInfo-bot-button"
                                         style={{cursor: 'pointer'}}>
                                        <img src={rocket} alt="rocket" className="submit-rocket-img"/>
                                        최종 제출
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