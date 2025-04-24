import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import supportLogo from "../../styles/images/support_logo.png";
import './qna.css'

import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

//출력 예시
const exampleData = "예시답변"


const QnA = () => {
    const [question, setQuestion] = useState('');
    const [chatList, setChatList] = useState([{type: "answer", text: "궁금한 점을 질문해보세요!"}]);

    function handleSubmit(question) {
        setChatList(prev => [...prev, {type: "question", text: question}])

    /*-----------------답변 가져오기---------
    apiClient.get('/api/certificate/info', {question})
        .then((res)=>{
            setAnswer(res.data.answer);
        });

     */
        setChatList(prev => [...prev, { ...prev[prev.length - 1] }, { type: "answer", text: exampleData }]);
    }


    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"챗봇 QnA"} imgSrc={supportLogo}
                                      backgroundColor={'linear-gradient(90deg, #4000FF 0%, #EFFD85 100%)'}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">QnA</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="qna-body-bot" style={{paddingBottom: '100px'}}>
                                {chatList.map((chat, index) => (
                                    <div key={index}
                                         className={chat.type === "question" ? "chat-question" : "chat-answer"}>
                                        <p>{chat.text}</p>
                                    </div>
                                ))}
                            </div>
                            <div className="chat-input-wrapper">
                                  <textarea
                                      placeholder="질문을 입력하세요"
                                      value={question}
                                      onKeyDown={(e) => {
                                          if (e.key === "Enter" && !e.shiftKey) {
                                              e.preventDefault();
                                              if(question.trim()) {
                                                  handleSubmit(question);
                                                  setQuestion("");
                                              }
                                          }
                                      }}
                                      onChange={(e) => setQuestion(e.target.value)}
                                      className="chat-input"
                                  />
                                <button
                                    className="chat-button"
                                    onClick={() => {
                                        if (question.trim()) {
                                            handleSubmit(question);
                                            setQuestion("");
                                        }
                                    }}
                                >
                                    ↑
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};

export default QnA;