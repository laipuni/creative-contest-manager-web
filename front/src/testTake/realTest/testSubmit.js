import React, {useEffect, useRef, useState} from 'react'
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import TestQuiz from "../../components/testQuiz/testQuiz";
import rocket from "../../styles/images/solve_icon.png";
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";

const TestSubmit = () => {
    const [text1, setText1] = useState('');
    const [file1, setFile1] = useState(null);
    const [text2, setText2] = useState('');
    const [file2, setFile2] = useState(null);
    const [level, setLevel] = useState('');
    const [quiz1, setQuiz1] = useState(null);
    const [quiz2, setQuiz2] = useState(null);
    const [answer1, setAnswer1] = useState(null);
    const [answer2, setAnswer2] = useState(null);

    const [contestInfo, setContestInfo] = useState(null);
    const [teamInfo, setTeamInfo] = useState(null);
    const navigate = useNavigate();

    /*--------------------사용자 팀 정보 들고오기(+유효한 대회 검증)-------------*/
    useEffect(() => {
        apiClient.get('/api/contests/latest')
            .then((res)=>{
                if(res.data.data){
                    const contestId = res.data.data.contestId;
                    setContestInfo(res.data.data);
                    apiClient.post(`/api/contests/${contestId}/join`)
                        .then((res)=>{
                            apiClient.get(`/api/contests/${contestId}/my-team`, {skipErrorHandler: true})
                                .then((res) => {
                                    setTeamInfo(res.data.data)
                                })
                                .catch((err)=>{
                                    if(err.response.status !== 400) alert(err.response.data.message);
                                })
                        })
                        .catch((err)=>{
                            alert(err.response.data.message);
                            navigate('/')});
                }
            })
            .catch((err)=>{})
    }, []);

    // 문제 가져오기
    useEffect(() => {
        if(teamInfo) {
            apiClient.get(`/api/problems/team/${teamInfo.teamId}`)
                .then((res) => {
                    const problems = res.data.data;
                    problems.forEach((problem) => {
                        if (problem.problemType === 'COMMON') {
                            setQuiz1(problem);
                        } else {
                            setQuiz2(problem);
                            if(problem.problemType === 'HIGH_NORMAL')
                                setLevel('고등/일반');
                            else
                                setLevel('초/중등');
                        }
                    });
                })
                .catch((e) => {
                });
            setLevel('초/중등');
        }
    }, [teamInfo])

    //답안 가져오기
    useEffect(() => {
        if (teamInfo) {
            apiClient.get(`/api/contests/${contestInfo.contestId}/team-solves`)
                .then((res) => {
                    const answerList = res.data.data.teamAnswerList;
                    answerList.forEach((answer) => {
                        if (answer.section === 'COMMON') {
                            setAnswer1(answer);
                            setText1(answer.content || '')
                        } else {
                            setAnswer2(answer);
                            setText2(answer.content || '')
                        }
                    });
                })
                .catch((e) => {
                });
        }
    }, [teamInfo]);


    const handleFileChange = (e,setFile) => {
        const file = e.target.files[0];
        if (!file) return;

        if (file.type !== "application/pdf") {
            alert("PDF 파일만 업로드 가능합니다");
            return;
        }

        setFile(file);
    }

    //답 제출
    const handleSubmitAnswer = () => {
        // 파일 체크
        if (!file1 || !file2) {
            alert('각 답안의 첨부파일을 모두 등록해주세요. (기존에 제출했던 답 있는 경우 덮어쓰기 필요)');
            return;
        }

        const submitSingleAnswer = (quiz, fileVal, textVal) => {
            const formData = new FormData();

            const requestData = {
                problemId: quiz.problemId,
                contents: textVal || ""
            };

            formData.append("request", new Blob(
                [JSON.stringify(requestData)],
                { type: "application/json" }
            ));

            formData.append("file", fileVal);

            return apiClient.post(`/api/contests/${contestInfo.contestId}/team-solves`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                skipErrorHandler: true
            });
        };

        Promise.all([
            submitSingleAnswer(quiz1, file1, text1),
            submitSingleAnswer(quiz2, file2, text2)
        ])
            .then(() => {
                alert('답안이 제출되었습니다.');
                navigate("/test/realTest/info")
            })
            .catch((err) => {
                const message = err?.response?.data?.message;
                if (message === 'Maximum upload size exceeded') {
                    alert('파일 용량 초과');
                } else {
                    alert(message || '제출 중 오류가 발생했습니다.');
                }
            });
    };



    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <TestQuiz
                            quizTitle='공통'
                            textVal={text1}
                            textOnChange={(e) => setText1(e.target.value)}
                            fileVal={file1}
                            fileOnChange={(e) => handleFileChange(e, setFile1)}
                            quiz={quiz1}
                            contestInfo={contestInfo}
                            answer={answer1}
                            teamInfo={teamInfo}
                        />
                        <TestQuiz
                            quizTitle={'수준별'}
                            textVal={text2}
                            textOnChange={(e) => setText2(e.target.value)}
                            fileVal={file2}
                            fileOnChange={(e) => handleFileChange(e, setFile2)}
                            quiz={quiz2}
                            contestInfo={contestInfo}
                            answer={answer2}
                            teamInfo={teamInfo}
                        />
                        <button className="registerInfo-bot-button"
                                onClick={handleSubmitAnswer}
                                style={{cursor: "pointer", alignSelf: "center"}}>
                            <img src={rocket} alt='rocket' className="submit-rocket-img"/>제출하기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TestSubmit