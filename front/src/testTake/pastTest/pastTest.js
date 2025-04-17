import React from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import './pastTest.css'
const PastTest = () => {
    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"기출문제 풀기"} imgSrc={testLogo} imageWidth='18%'
                                      backgroundColor={'linear-gradient(90deg, #FF6200 0%, #FDEB85 100%)'}/>
                        <div className="pastTest-container">
                            <div className="pastTest-top-container">
                                <div className="pastTest-top-category-container">
                                    <div className="pastTest-top-category">초/중등</div>
                                    <div className="pastTest-top-category">고등/일반</div>
                                    <div className="pastTest-top-category">공통</div>
                                </div>
                                <div className="pastTest-top-underline"></div>
                            </div>
                            <div className="pastTest-bot-container">
                                <div className="pastTest-bot-title-container">
                                    <p className="pastTest-bot-leftTitle">파일</p>
                                    <div className="pastTest-verticalLine"></div>
                                    <p className="pastTest-bot-rightTitle">작성일</p>
                                </div>
                                <div className="pastTest-bot-quiz-container">
                                    <div className="pastTest-bot-title-container"
                                         style={{background: 'white', gap: '0px', padding: '0px 20px'}}>
                                        <p className="pastTest-bot-leftTitle"
                                           style={{textAlign: 'left', width: 'fit-content'}}>2025 기출 문제</p>
                                        <p className="pastTest-bot-file">📄</p>
                                        <p className="pastTest-bot-rightTitle"
                                        style={{width: '76%', textAlign: 'right'}}>2025-06-10</p>
                                    </div>
                                    <div className="pastTest-bot-title-container"
                                         style={{background: 'white', gap: '0px', padding: '0px 20px'}}>
                                        <p className="pastTest-bot-leftTitle"
                                            style={{textAlign: 'left', width: 'fit-content'}}>2024 기출 문제</p>
                                        <p className="pastTest-bot-file">📄</p>
                                        <p className="pastTest-bot-rightTitle"
                                           style={{width: '76%', textAlign: 'right'}}>2024-12-25</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default PastTest;