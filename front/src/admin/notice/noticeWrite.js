import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import './noticeWrite.css';
import React, {useState} from "react";
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";

const NoticeWrite = () => {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [file, setFile] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new FormData();

        const requestData = {
            title,
            content
        };

        formData.append("request", new Blob(
            [JSON.stringify(requestData)],
            { type: "application/json" }
        ));
        if(file)
            formData.append('files', file);

        apiClient.post('api/admin/notices', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }})
            .then((res) => {
                alert('공지가 등록되었습니다.')
                navigate('/admin/notices');
            })
            .catch((err) => {});
    };

    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='800px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '97%'}}>
                            <div className="admin-teamList-title">공지사항 작성</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                    </div>
                    <div className="noticeWrite-container">
                        <form onSubmit={handleSubmit} style={{display: "flex", flexDirection: "column", gap: "10px"}}>
                            <input
                                type="text"
                                placeholder="제목을 입력하세요"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                            />
                            <textarea
                                style={{resize: "none"}}
                                placeholder="내용을 입력하세요"
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                rows={15}
                            />
                            <input
                                type="file"
                                onChange={(e) => setFile(e.target.files[0])}
                            />
                            <button type="submit">글 등록</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeWrite