import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import './noticeWrite.css';
import React, {useState} from "react";
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";
import {FaFileAlt} from "react-icons/fa";

const NoticeWrite = () => {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [files, setFiles] = useState([]);
    const navigate = useNavigate();
    const fileInputRef = React.useRef(null);

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
        files.forEach(file => {
            formData.append("files", file);
        });

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

    const handleFileUpload = (e) => {
        const files = Array.from(e.target.files);
        setFiles((prev) => [...prev, ...files]);
        e.target.value = null;
    };

    const handleDeleteFile = (file) => {
        setFiles((prev) => prev.filter((f) => f !== file));
    };

    const handleFakeClick = () => {
        if (fileInputRef.current) {
            fileInputRef.current.click();
        }
    };

    const handleDownloadLocalFile = (file) => {
        const url = URL.createObjectURL(file);
        const a = document.createElement('a');
        a.href = url;
        a.download = file.name;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url); // 메모리 해제
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
                                multiple
                                style={{display: 'none'}}
                                ref={fileInputRef}
                                onChange={handleFileUpload}
                            />
                            <div style={{display: 'flex', flexDirection: 'row', gap: '10px'}}>
                                <button type="button" style={{height: 'fit-content'}} onClick={handleFakeClick}>
                                    파일 첨부
                                </button>
                                {files.length > 0 ? (
                                    <ul className="noticeDetail-content-text" style={{
                                        listStyle: 'none',
                                        paddingLeft: '0',
                                        marginTop: '5px',
                                        flexDirection: 'column'
                                    }}>
                                        {files.map((file, index) => (
                                            <li key={index} className="noticeDetail-file-item" style={{width: '100%'}}>
                                                <FaFileAlt className="noticeDetail-file-icon"/>
                                                <span
                                                    onClick={()=>handleDownloadLocalFile(file)}
                                                    className="noticeDetail-file-name">{file.name}</span>
                                                <button
                                                    type="button"
                                                    onClick={() => handleDeleteFile(file)}
                                                    style={{marginLeft: '10px', fontSize: '8px'}}
                                                >
                                                    X
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="noticeDetail-content-text">선택된 파일 없음</p>
                                )}
                            </div>
                            <button type="submit">글 등록</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeWrite