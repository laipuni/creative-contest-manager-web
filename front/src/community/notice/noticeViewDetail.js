import React, {useEffect, useState} from "react";
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import apiClient from "../../templates/apiClient";
import {useNavigate, useParams} from "react-router-dom";
import './noticeViewDetail.css'
import {format} from "date-fns";
import { FaFileAlt } from 'react-icons/fa';

const NoticeViewDetail = () => {
    const { noticeId } = useParams();
    const [notice, setNotice] = useState(null);
    const navigate = useNavigate();
    useEffect(() => {
        console.log(noticeId);
        apiClient.get(`/api/notices/${noticeId}`)
            .then((res)=>{
                setNotice(res.data.data);
            })

    }, []);

    const handleDownload = (fileId, fileName) => {
        apiClient.get(`/api/notices/${noticeId}/files/${fileId}/download`, {
            responseType: 'blob'  // 반드시 추가!
        })
            .then((res)=>{
                const url = window.URL.createObjectURL(new Blob([res.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', fileName);
                document.body.appendChild(link);
                link.click();
                link.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch((err)=>{})
    }
    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"공지사항"} imgSrc={trophyLogo} imageWidth='18%'/>
                        {notice &&
                            <div className="noticeDetail-container">
                                <div className="noticeDetail-underLine"></div>
                                <div className="noticeDetail-content-container">
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 제목</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        <p className="noticeDetail-content-text">{notice.title}</p>
                                    </div>
                                </div>
                                <div className="noticeDetail-content-container" style={{gap: '100px'}}>
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 작성자</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        <p className="noticeDetail-content-text">{notice.writer}</p>
                                    </div>
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 등록일</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        <p className="noticeDetail-content-text">{format(new Date(notice.createAt), 'yyyy-MM-dd')}</p>
                                    </div>
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 조회수</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        <p className="noticeDetail-content-text">{notice.viewCount}</p>
                                    </div>
                                </div>
                                <div className="noticeDetail-content-container">
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 이메일</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        <p className="noticeDetail-content-text">{notice.writerEmail}</p>
                                    </div>
                                </div>
                                <div className="noticeDetail-content-container">
                                    <div className="noticeDetail-content-text-container">
                                        <p className="noticeDetail-content-title">> 첨부파일</p>
                                        <div className="noticeDetail-verticalLine"></div>
                                        {notice.fileList.length > 0 ? (
                                            <ul className="noticeDetail-content-text" style={{
                                                listStyle: 'none',
                                                paddingLeft: '0',
                                                marginTop: '15px'
                                            }}>
                                                {notice.fileList.map((file) => (
                                                    <li key={file.fileId} className="noticeDetail-file-item"
                                                        onClick={() => handleDownload(file.fileId, file.fileName)}>
                                                        <FaFileAlt className="noticeDetail-file-icon"/>
                                                        <span className="noticeDetail-file-name">{file.fileName}</span>
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <p className="noticeDetail-content-text">없음</p>
                                        )}
                                    </div>
                                </div>
                                <div className="noticeDetail-main-contentbox">
                                    <p className="noticeDetail-content-text"
                                       style={{lineHeight: '1.5', wordBreak: 'break-word', whiteSpace: 'pre-wrap'}}>{notice.content}</p>
                                </div>
                                <div className="noticeDetail-buttonbox">
                                    <button className="noticeDetail-button"
                                    onClick={()=>navigate('/community/notice')}>목록</button>
                                </div>
                            </div>}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeViewDetail;