import React, {useEffect, useRef, useState} from 'react'
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import TestQuiz from "../../components/testQuiz/testQuiz";
import rocket from "../../styles/images/solve_icon.png";
import apiClient from "../../templates/apiClient";

const TestSubmit = () => {
    const [text1, setText1] = useState('');
    const [file1, setFile1] = useState(null);
    const [text2, setText2] = useState('');
    const [file2, setFile2] = useState(null);
    const [level, setLevel] = useState('');
    const [quiz1, setQuiz1] = useState(null);
    const [quiz2, setQuiz2] = useState(null);

    /*--------------------사용자 시험 정보 들고오기-------------*/
    useEffect(() => {
        setLevel('초/중등');
        /*------rest
        apiClient.get('/api/v1/test/submit') //setText, setFile (제출 정보 들고오기)
        apiClient.get('/api/v1/test') // setQuiz (문제 정보 들고오기)
         */
    }, [])

    const handleFileChange = (e,setFile) => {
        const file = e.target.files[0];
        if (!file) return;

        if (file.type !== "application/pdf") {
            alert("PDF 파일만 업로드 가능합니다");
            return;
        }

        setFile(file);
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        apiClient.post('/api/v1/test/submit',
            {
                    text1, text2, file1, file2
            })
    }

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <form onSubmit={handleSubmit} className="testInfo-main-container">
                        <TestQuiz quizTitle='문제 1 (공통)' textVal={text1} textOnChange={(e) => setText1(e.target.value)}
                                  fileVal={file1} fileOnChange={(e) => handleFileChange(e, setFile1)} quiz={quiz1}/>
                        <TestQuiz quizTitle={'문제 2 (' + level + ')'} textVal={text2}
                                  textOnChange={(e) => setText2(e.target.value)}
                                  fileVal={file2} fileOnChange={(e) => handleFileChange(e, setFile2)} quiz={quiz2}/>
                        <div className="registerInfo-bot-buttonbox">
                            <button className="registerInfo-bot-button">
                                <img src={rocket} alt='rocket' className="submit-rocket-img"/>제출하기</button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    )
}

export default TestSubmit