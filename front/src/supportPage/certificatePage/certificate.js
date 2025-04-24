import React, {useEffect, useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import supportLogo from "../../styles/images/support_logo.png";
import "./certificate.css"

import {format} from 'date-fns'
import apiClient from "../../templates/apiClient";

//출력 예시
const exampleData = [
    {
        testYear: "2024",
        certificateType: "예선합격증",
        fileLink: "example_File_2024_prelim",
    },
    {
        testYear: "2023",
        certificateType: "본선참가확인서",
        fileLink: "example_File_2023_final",
    },
    {
        testYear: "2022",
        certificateType: "수료증",
        fileLink: "example_File_2022_completion",
    },
    {
        testYear: "2021",
        certificateType: "참가확인서",
        fileLink: "example_File_2021_participation",
    },
    {
        testYear: "2020",
        certificateType: "예선합격증",
        fileLink: "example_File_2020_prelim",
    }
];


const Certificate = () => {
    const [testYear, setTestYear] = useState('');
    const [certificateType, setCertificateType] = useState('');
    const [fileLink, setFileLink] = useState('');

    useEffect(() => {
        setTestYear(exampleData.testYear);
        setCertificateType(exampleData.certificateType);
        setFileLink(exampleData.fileLink);
    }, []);

    /*-----------------접수 내역 가져오기---------
    apiClient.get('/api/certificate/info'})
        .then((res)=>{
            setTestYear(exampleData.testYear);
            setCertificateType(exampleData.certificateType);
            setFileLink(exampleData.fileLink);
        });

     */

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"증명서 발급"} imgSrc={supportLogo}
                            backgroundColor={'linear-gradient(90deg, #4000FF 0%, #EFFD85 100%)'}/>
                        <div className="registerInfo-body-container">
                            <div className="registerInfo-body-top">
                                <p className="registerInfo-top-title">응시 내역</p>
                                <div className="registerInfo-underline"></div>
                            </div>
                            <div className="registerInfo-body-bot">
                                <div className="registerInfo-bot-title">
                                    <p className="registerInfo-bot-text">응시년도</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">유형</p>
                                    <div className="registerInfo-bot-line"></div>
                                    <p className="registerInfo-bot-text">파일</p>
                                </div>
                                <ul className="certificate-list">
                                    {exampleData.map(item => (
                                        <li key={item.id}>
                                            <div className="registerInfo-bot-content">
                                                <p className="certificate-text">{item.testYear}</p>
                                                <p className="certificate-text">{item.certificateType}</p>
                                                <p className="certificate-text">{item.fileLink}</p>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};

export default Certificate;