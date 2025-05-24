import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import React, {useEffect, useRef, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import apiClient from "../../templates/apiClient";
import {FaFileAlt} from "react-icons/fa";

const NoticeDetail = () => {
    const { noticeId } = useParams();
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [existingFiles, setExistingFiles] = useState([]);
    const [newFiles, setNewFiles] = useState([]);
    const [deleteFileIds, setDeleteFileIds] = useState([]);
    const navigate = useNavigate();
    const fileInputRef = useRef(null);

    useEffect(() => {
        apiClient.get(`/api/admin/notices/${noticeId}`)
            .then((res)=>{
                setTitle(res.data.data.title)
                setContent(res.data.data.content);
                setExistingFiles(res.data.data.fileList);
            })
            .catch((err)=>{})
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();


        const formData = new FormData();

        const requestData = {
            noticeId,
            title,
            content,
            deleteFileIds
        };

        formData.append("request", new Blob(
            [JSON.stringify(requestData)],
            { type: "application/json" }
        ));
        newFiles.forEach(file => {
            formData.append("files", file);
        });

        apiClient.patch('api/admin/notices', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }})
            .then((res) => {
                alert('공지가 수정되었습니다.')
                navigate(-1);
            })
            .catch((err) => {});
    };

    const handleDeleteFile = (file, isExisting) => {
        if (isExisting) {
            setDeleteFileIds((prev) => [...prev, file.fileId]);
            setExistingFiles((prev) => prev.filter((f) => f.fileId !== file.fileId));
        } else {
            setNewFiles((prev) => prev.filter((f) => f !== file));
        }
    };

    const handleFileUpload = (e) => {
        const files = Array.from(e.target.files);
        setNewFiles((prev) => [...prev, ...files]);
        e.target.value = null;
    };

    const handleDeleteNotice = () => {
        const result = window.confirm('삭제 후 복구가 불가능합니다. 정말 삭제하시겠습니까?');
        if(!result) return;
        apiClient.delete('/api/admin/notices', {data: noticeId})
            .then((res)=>{
                alert('삭제되었습니다');
                navigate(-1);
            })
            .catch((err)=>{})
    }

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

    const handleDownload = (fileId, fileName) => {
        apiClient.get(`/api/notices/${noticeId}/files/${fileId}/download`)
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
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='800px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '97%'}}>
                            <div className="admin-teamList-title">공지사항 수정</div>
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
                                ref={fileInputRef}
                                multiple
                                style={{display: 'none'}}
                                onChange={handleFileUpload}
                            />
                            <div style={{display: 'flex', flexDirection: 'row', gap: '10px'}}>
                                <button
                                    type="button"
                                    style={{height: 'fit-content'}}
                                    onClick={handleFakeClick}
                                >
                                    파일 첨부
                                </button>

                                {(existingFiles.length === 0 && newFiles.length === 0) ? (
                                    <p className="noticeDetail-content-text">선택된 파일 없음</p>
                                ) : (
                                    <ul
                                        className="noticeDetail-content-text"
                                        style={{
                                            listStyle: 'none',
                                            paddingLeft: '0',
                                            marginTop: '5px',
                                            flexDirection: 'column',
                                        }}>
                                        {/* 기존 파일 목록 */}
                                        {existingFiles.map((file, index) => (
                                            <li key={`existing-${file.id}`} className="noticeDetail-file-item"
                                                style={{width: '100%'}}>
                                                <FaFileAlt className="noticeDetail-file-icon"/>
                                                <span
                                                    onClick={() => handleDownload(file.fileId, file.fileName)}
                                                    className="noticeDetail-file-name"
                                                >
                                                    {file.fileName}
                                                </span>
                                                <button
                                                    type="button"
                                                    onClick={() => handleDeleteFile(file, true)}
                                                    style={{marginLeft: '10px', fontSize: '8px'}}
                                                >
                                                    X
                                                </button>
                                            </li>
                                        ))}

                                        {/* 새로 업로드한 파일 목록 */}
                                        {newFiles.map((file, index) => (
                                            <li key={`new-${index}`} className="noticeDetail-file-item"
                                                style={{width: '100%'}}>
                                                <FaFileAlt className="noticeDetail-file-icon"/>
                                                <span
                                                    onClick={() => handleDownloadLocalFile(file)}
                                                    className="noticeDetail-file-name"
                                                >
                                                {file.name}
                                                </span>
                                                <button
                                                    type="button"
                                                    onClick={() => handleDeleteFile(file, false)}
                                                    style={{marginLeft: '10px', fontSize: '8px'}}
                                                >
                                                    X
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                            <div style={{
                                display: 'flex',
                                flexDirection: 'row',
                                gap: '5px',
                                width: '100%',
                                justifyContent: 'flex-end'
                            }}>
                                <button type="submit">수정</button>
                                <button type="button"
                                        onClick={handleDeleteNotice}>삭제
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeDetail