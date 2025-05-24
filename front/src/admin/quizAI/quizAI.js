import React, {useEffect, useRef, useState} from "react";
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";


//출력 예시
const exampleData = "예시답변"
const exampleQuestion = [{type: "answer", text: "궁금한 점을 질문해보세요!"},
    {type: "answer", text: "답변 생성 중"},
    {type: "answer", text: "답변을 생성하지 못했습니다. 다시 시도해주세요"}]


const QuizAI = () => {
    const [question, setQuestion] = useState('');
    const [chatList, setChatList] = useState(exampleQuestion);
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


    function handleSubmit(question) {
        //1. 질문 추가 + 스크롤 내리기
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
                                onClick={() => {
                                    window.scrollTo({top: document.body.scrollHeight, behavior: "smooth"});
                                }}
                            >
                                <span>▼</span>
                            </button>
                        )
                        }
                        <div className="chat-input-wrapper">
                <textarea
                    placeholder="질문을 입력하세요"
                    value={question}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                            e.preventDefault();
                            if (question.trim()) {
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
    )
}

export default QuizAI;