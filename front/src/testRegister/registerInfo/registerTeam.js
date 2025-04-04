import React, {useState} from 'react';
import './registerInfo.css';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import './registerTeam.css'
import {useNavigate} from "react-router-dom";
import {Link} from "react-router-dom";
import apiClient from "../../templates/apiClient";

const RegisterTeam = () => {
    const [teamName, setTeamName] = useState("");
    const [teamMate1, setTeamMate1] = useState("");
    const [teamMate2, setTeamMate2] = useState("");
    const navigate = useNavigate();

    const handleRegisterTeam = (e) => {
        e.preventDefault();
        if (!teamMate1 && !teamMate2) {
            alert('팀원을 1명 이상 등록해주세요.')
            return;
        }
        /*-----------------접수하기---------
        apiClient.post('/api/register/team', {teamName, teamMate1, teamMate2})
       .then((res)=>{
          navigate('/register/info');
       });

    */
        navigate('/');
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
                                    type="text" value={teamMate1} readOnly
                                    onChange={(e)=>setTeamMate1(e.target.value)}></input>
                                    <button type="button" className="registerTeam-enter-button">등록</button>
                                </div>
                                <div className="registerTeam-bot-textbox">
                                    <p className="registerTeam-bot-title">팀원2</p>
                                    <div className="registerTeam-bot-line"></div>
                                    <input className="registerTeam-bot-input"
                                           type="text" value={teamMate2} readOnly
                                           onChange={(e) => setTeamMate2(e.target.value)}></input>
                                    <button type="button" className="registerTeam-enter-button">등록</button>
                                </div>
                                <p className="registerTeam-bot-warning">※ 팀원은 최소 1명 이상 등록해야 합니다.</p>
                                <div className="registerTeam-bot-buttonbox">
                                <button type="submit" className="registerTeam-register-button">접수하기</button>
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