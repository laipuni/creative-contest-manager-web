import React, {useEffect, useState} from 'react';
import './registerInfo.css';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import './registerTeam.css'
import {useLocation, useNavigate} from "react-router-dom";
import {Link} from "react-router-dom";
import apiClient from "../../templates/apiClient";

const RegisterTeam = () => {
    const [teamName, setTeamName] = useState("");
    const [teamMate1, setTeamMate1] = useState("");
    const [teamMate2, setTeamMate2] = useState("");
    const [contestInfo, setContestInfo] = useState({});
    const navigate = useNavigate();

    const location = useLocation();
    const { teamInfo } = location.state || {};

    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res)=>{
                if(res.data.data){
                    setContestInfo(res.data.data);
                }
            })
            .catch((e)=>{})
    }, []);

    useEffect(() => {
        if (teamInfo) {
            const leaderId = teamInfo.leaderLoginId;
            const members = teamInfo.members
                .filter(member => member.loginId !== leaderId)
                .map(member => member.loginId); // ← loginId만 추출

            setTeamName(teamInfo.teamName);

            if (members.length === 2) {
                setTeamMate1(members[0]);
                setTeamMate2(members[1]);
            } else {
                setTeamMate1(members[0] || '');
                setTeamMate2('');
            }
        }
    }, [teamInfo]);

    const handleRegisterTeam = (e) => {
        e.preventDefault();
        if (!teamMate1 && !teamMate2) {
            alert('팀원을 1명 이상 등록해주세요.')
            return;
        }
        const memberIds = [teamMate1, teamMate2].filter(id => !!id);
        console.log(memberIds);
        /*-----------------접수하기---------*/
        if(!teamInfo) {
            apiClient.post('/api/teams', {
                teamName,
                contestId: contestInfo.contestId,
                memberIds
            }, {skipErrorHandler: true})
                .then((res) => {
                    navigate('/register/info');
                })
                .catch((err) => {
                    alert(err.response.data.message);
                })
        }
        /*----------------수정하기-----------*/
        else {
            const memberIds = [teamMate1, teamMate2].filter(id => !!id);
            apiClient.patch(`/api/teams/${teamInfo.teamId}`, {
                teamName,
                memberIds,
                contestId: contestInfo.contestId
            }, {skipErrorHandler: true})
                .then((res)=>{
                    navigate('/register/info');
                })
                .catch((err) => {
                    alert(err.response.data.message);
                })
        }
    }

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
                                <p className="registerInfo-top-title">예선 접수</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <form onSubmit={handleRegisterTeam} className="registerTeam-body-bot">
                                <div className="registerTeam-bot-textbox">
                                    <p className="registerTeam-bot-title">팀명</p>
                                    <div className="registerTeam-bot-line"></div>
                                    <input className="registerTeam-bot-input" type="text"
                                           value={teamName}
                                           required
                                           minLength='2' maxLength='7'
                                           onChange={(e) => setTeamName(e.target.value)}
                                    ></input>
                                </div>
                                <p className="registerTeam-bot-warning">※ 팀명은 2자 이상 7자 이내로 작성해 주세요.</p>
                                <div className="registerTeam-bot-textbox">
                                    <p className="registerTeam-bot-title">팀원1</p>
                                    <div className="registerTeam-bot-line"></div>
                                    <input className="registerTeam-bot-input"
                                    type="text" value={teamMate1}
                                    onChange={(e)=>setTeamMate1(e.target.value)}></input>
                                </div>
                                <div className="registerTeam-bot-textbox">
                                    <p className="registerTeam-bot-title">팀원2</p>
                                    <div className="registerTeam-bot-line"></div>
                                    <input className="registerTeam-bot-input"
                                           type="text" value={teamMate2}
                                           onChange={(e) => setTeamMate2(e.target.value)}></input>
                                </div>
                                <p className="registerTeam-bot-warning">※ 팀원의 아이디를 정확하게 입력해주세요.</p>
                                <div className="registerTeam-bot-buttonbox">
                                    {!teamInfo && <button type="submit" className="registerTeam-register-button">접수하기</button>}
                                    {teamInfo && <button type="submit" className="registerTeam-register-button">수정하기</button>}
                                    <button className="registerTeam-register-button" type="button"
                                            style={{color: 'black', background: 'rgba(95, 164, 255, 0.09)'}}
                                    onClick={()=>{navigate('/register/info')}}>나가기</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};


export default RegisterTeam;