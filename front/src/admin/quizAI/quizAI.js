import React, {useEffect, useRef, useState} from "react";
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import apiClient from "../../templates/apiClient";

const QuizAI = () => {
    const [topic, setTopic] = useState('논리형');
    const [level, setLevel] = useState('상');
    const [count, setCount] = useState(null);
    const [chatList, setChatList] = useState([]);
    const [showScrollButton, setShowScrollButton] = useState(false); //아래 내리기 버튼
    const endRef = useRef(null);  // 마지막 요소 참조
    //스크롤 감지
    useEffect(() => {
        const handleScroll = () => {
            const scrollY = window.scrollY;
            const windowHeight = window.innerHeight;
            const docHeight = document.documentElement.scrollHeight;

            const atBottom = scrollY + windowHeight >= docHeight - 200;
            setShowScrollButton(!atBottom);
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [chatList]);


    function handleSubmit(e) {
        e.preventDefault();
        //1. 질문 추가 + 스크롤 내리기
        setChatList(prev => [...prev, {type: "question", text: `${topic}, ${level}, ${count}개 생성`}])
        //2. 로딩 메시지
        const loadingMessage = { type: "answer", text: "답변 생성 중" };
        setChatList(prev => [...prev, loadingMessage]);

        // 3. 실제 API 요청
        apiClient.post('/api/admin/ai/generate', { topic, level, count })
            .then((res) => {
                // "답변 생성 중..." → 실제 답변으로 교체
                setChatList((prev) => {
                    const newAnswers = res.data.data.map((item) => ({
                        type: "answer",
                        text: `${item.question}\n\n${item.answer}`,
                    }));

                    return [...prev.slice(0, prev.length - 1), ...newAnswers];
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


    }
    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='800px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '97%'}}>
                            <div className="admin-teamList-title">AI 문제생성</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                    </div>
                    <div className="noticeWrite-container">
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
                            <div ref={endRef}/>
                        </div>
                        {showScrollButton && (
                            <button
                                className="scroll-button"
                                style={{marginBottom: '10px'}}
                                onClick={() => {
                                    window.scrollTo({top: document.body.scrollHeight, behavior: "smooth"});
                                }}
                            >
                                <span>▼</span>
                            </button>
                        )
                        }
                        <div className="chat-input-wrapper">
                            <form className="chat-input"
                                  onSubmit={(e) => {
                                      e.preventDefault();
                                      if (count > 0 && topic && level) handleSubmit(e);
                                      else alert('항목을 모두 기입해주세요');
                                  }}>
                                <div style={{marginBottom: "8px"}}>
                                    <label>
                                        유형:
                                        <select
                                            value={topic}
                                            style={{marginLeft: '10px'}}
                                            onChange={(e) => setTopic(e.target.value)}>
                                            <option value="논리">논리형</option>
                                            <option value="수학">수학형</option>
                                            <option value="상식">상식형</option>
                                            <option value="열린문제">열린문제</option>
                                        </select>
                                    </label>
                                </div>

                                <div style={{marginBottom: "8px"}}>
                                    <label>
                                        난이도:
                                        <select value={level}
                                                onChange={(e) => setLevel(e.target.value)}
                                                style={{marginLeft: '10px'}}>
                                            <option value="상">상</option>
                                            <option value="중">중</option>
                                            <option value="하">하</option>
                                        </select>
                                    </label>
                                </div>

                                <div style={{marginBottom: "8px"}}>
                                    <label>
                                        개수:
                                        <input
                                            type="text"
                                            onChange={(e) => {
                                                const value = e.target.value;
                                                if (/^\d*$/.test(value)) {
                                                    setCount(Number(value));
                                                }
                                            }}
                                            inputMode="numeric"
                                            pattern="[0-9]*"
                                            style={{marginLeft: '10px', width: "30px"}}
                                        />
                                    </label>
                                </div>
                            </form>
                                <button
                                    className="chat-button"
                                    style={{padding:'5px'}}
                                    onClick={(e) => {
                                        {
                                            handleSubmit(e);
                                        }
                                    }}
                                >
                                    생성
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            )
            }

            export default QuizAI;