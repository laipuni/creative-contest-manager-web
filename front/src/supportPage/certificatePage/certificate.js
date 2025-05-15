import React, { useEffect, useState } from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import Pagination from  "../../styles/pagination.css";
import supportLogo from "../../styles/images/support_logo.png";
import "./certificate.css";
import { format } from 'date-fns';
import apiClient from "../../templates/apiClient";

const Certificate = () => {
    const [certificates, setCertificates] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [pdfUrl, setPdfUrl] = useState(null);


    useEffect(() => {
        fetchCertificates(currentPage);
    }, [currentPage]);

    const fetchCertificates = (page) => {
        apiClient.get('/api/v1/certificates', {
            params: { page: page - 1 } // 백엔드는 0부터 시작
        }).then(res => {
            const data = res.data.data;
            setCertificates(data.certificateDtoList);
            setTotalPages(data.totalPage);
        }).catch(err => {
        });
    };

    const downloadCertificate = (certificateId, title) => {
        apiClient.get(`/api/certificates/${certificateId}`, {
            responseType: 'blob'
        }).then(res => {
            const blob = new Blob([res.data], { type: 'application/pdf' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `${title}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        }).catch(err => {;
        });
    };

    const getCertificateLabel = (type) => {
        return type === "PRELIMINARY" ? "예선참가확인증" : "본선참가확인증";
    };

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%" />
                <div className="testInfo-content-container">
                    <Sidebar />
                    <div className="testInfo-main-container">
                        <CategoryLogo
                            logoTitle={"증명서 발급"}
                            imgSrc={supportLogo}
                            backgroundColor={'linear-gradient(90deg, #4000FF 0%, #EFFD85 100%)'}
                        />
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
                                    {certificates.map(cert => (
                                        <li key={cert.certificateId}>
                                            <div className="registerInfo-bot-content">
                                                <p className="certificate-text">{format(new Date(cert.createdAt), 'yyyy')}</p>
                                                <p className="certificate-text">{getCertificateLabel(cert.certificateType)}</p>
                                                <button
                                                    className="certificate-download-button"
                                                    onClick={() => downloadCertificate(cert.certificateId, cert.title)}
                                                >
                                                    {cert.title}.pdf
                                                </button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                                {totalPages > 1 && (
                                    <div className="pastTest-pagination-container">
                                        <Pagination
                                            totalPages={totalPages}
                                            currentPage={currentPage}
                                            onPageChange={setCurrentPage}
                                        />
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Certificate;
