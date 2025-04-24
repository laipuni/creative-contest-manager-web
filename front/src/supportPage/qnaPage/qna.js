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
const exampleQuestion = [{type: "answer", text: "궁금한 점을 질문해보세요!"},
    {type: "answer", text: "답변 생성 중"},
    {type: "answer", text: "답변을 생성하지 못했습니다. 다시 시도해주세요"}]


const QnA = () => {
    const [question, setQuestion] = useState('');
    const [chatList, setChatList] = useState(exampleQuestion);

    function handleSubmit(question) {
        //1. 질문 추가
        setChatList(prev => [...prev, {type: "question", text: question}])
        //2. 로딩 메시지
        const loadingMessage = { type: "answer", text: "답변 생성 중" };
        setChatList(prev => [...prev, loadingMessage]);

        /*
        // 3. 실제 API 요청
        apiClient.get('/api/certificate/info', { params: { question } })
            .then((res) => {
                // "답변 생성 중..." → 실제 답변으로 교체
                setChatList(prev => {
                    const updated = [...prev];
                    updated[updated.length - 1] = { type: "answer", text: res.data.answer };
                    return updated;
                });
            })
            .catch((err) => {
                // "답변 생성 중..." → 에러 메시지로 교체
                setChatList(prev => {
                    const updated = [...prev];
                    updated[updated.length - 1] = {
                        type: "answer",
                        text: "답변을 생성하지 못했습니다. 다시 시도해주세요"
                    };
                    return updated;
                });
            });
         */
        setChatList(prev => {
            const updated = [...prev];
            updated[updated.length - 1] = {
                type: "answer", text: exampleData
            };
            return updated;
        });

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
                                        {chat.text === "답변 생성 중" && (
                                            <span className="dots-loading">
                                                <span>.</span><span>.</span><span>.</span>
                                            </span>
                                        )}
                                        {chat.text === "답변을 생성하지 못했습니다. 다시 시도해주세요" && (
                                            <span className="error-symbol">❗</span>
                                        )}
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