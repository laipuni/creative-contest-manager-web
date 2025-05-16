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

const RegisterInfo = () => {
    const [teamInfo, setTeamInfo] = useState(null)
    const [contestInfo, setContestInfo] = useState(null);

    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res)=>{
                if(res.data.data){
                    setContestInfo(res.data.data);
                    apiClient.get(`/api/contests/${res.data.data.contestId}/my-team`, {skipErrorHandler: true})
                        .then((res) => {
                            setTeamInfo(res.data.data)
                        })
                        .catch((err)=>{
                            if(err.response.status !== 400) alert(err.response.data.message);
                        })
                }
            })
            .catch((err)=>{})
    }, []);

    const handleDeleteTeam = () => {
        apiClient.delete('/api/teams', {
            data: {teamId: teamInfo.teamId, contestId: contestInfo.contestId}, skipErrorHandler: true})
            .then((res)=>{
                setTeamInfo(null);
            })
            .catch((err)=>{alert(err.response.data.message)});
    }


    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={`대회참가 접수`} imgSrc={trophyLogo}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">접수 내역</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="registerInfo-body-bot">
                                <div className="registerInfo-bot-title">
                                    <p className="registerInfo-bot-text">팀명</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">팀원1</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">팀원2</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">접수일자</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">팀장</p>
                                </div>
                                {teamInfo && (
                                    <div className="registerInfo-bot-content">
                                        <p className="registerInfo-bot-text">{teamInfo.teamName}</p>
                                        {(() => {
                                            const members = teamInfo.members.filter(member => member.name !== teamInfo.leaderName);
                                            const filledMembers = [...members, ...Array(2 - members.length).fill({ name: 'x' })];
                                            return filledMembers.map((member, index) => (
                                                <p className="registerInfo-bot-text" key={index}>{member.name}</p>
                                            ));
                                        })()}
                                        <p className="registerInfo-bot-text">
                                            {format(new Date(teamInfo.createdAt), 'yyyy-MM-dd')}
                                        </p>
                                        <p className="registerInfo-bot-text">{teamInfo.leaderName}</p>
                                    </div>
                                )}

                                <div className="registerInfo-bot-buttonbox">
                                    {teamInfo &&
                                        <Link to="/register/team" state={{teamInfo}} className="registerInfo-bot-button" >
                                            <div className="submit-rocket-img" style={{width:'0px'}}/>
                                            수정하기</Link>}
                                    <Link to="/register/team" className="registerInfo-bot-button">
                                        <img src={rocket} alt='rocket' className="submit-rocket-img"/>
                                        접수하기</Link>
                                    {teamInfo &&
                                        <div onClick={handleDeleteTeam} className="registerInfo-bot-button" style={{cursor: 'pointer'}}>
                                            <div className="submit-rocket-img" style={{width: '0px'}}/>
                                            삭제하기</div>}
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