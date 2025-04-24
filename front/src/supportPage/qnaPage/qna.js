import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import supportLogo from "../../styles/images/support_logo.png";

import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

//출력 예시
const exampleData1 = "예시답변1"
const exampleData2 = "예시답변2"


const QnA = () => {
    const [question, setQuestion] = useState('');
    const [answer, setAnswer] = useState('');


    /*-----------------답변 가져오기---------
    apiClient.get('/api/certificate/info', {question})
        .then((res)=>{
            setAnswer(res.data.answer);
        });

     */

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
                            <div className="registerInfo-body-bot">

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};

export default QnA;