import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import {FaSearch} from "react-icons/fa";
import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import apiClient from "../../templates/apiClient";
import {format} from "date-fns";
import './noticeManage.css'

function Pagination({ totalPages, currentPage, onPageChange }) {
    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    const isDisabled = totalPages === 0;

    return (
        <div className="pagination">
            <button disabled={isDisabled || currentPage === 1} onClick={() => onPageChange(1)}>«</button>
            <button disabled={isDisabled || currentPage === 1} onClick={() => onPageChange(currentPage - 1)}>‹</button>

            {pages.map(page => (
                <button
                    key={page}
                    className={page === currentPage ? 'active' : ''}
                    onClick={() => onPageChange(page)}
                >
                    {page}
                </button>
            ))}

            <button disabled={isDisabled || currentPage === totalPages} onClick={() => onPageChange(currentPage + 1)}>›</button>
            <button disabled={isDisabled || currentPage === totalPages} onClick={() => onPageChange(totalPages)}>»</button>
        </div>
    );
}

const NoticeManage = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [lastPage, setLastPage] = useState(0);
    const [searchType, setSearchType] = useState('loginId');
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [notices, setNotices] = useState([]);
    const [isEdited, setIsEdited] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const params = searchKeyword
            ? { page: currentPage - 1, keyword: searchKeyword, search_type: searchType }
            : { page: currentPage - 1 };
        apiClient.get('/api/admin/notices/search', {params})
            .then((res)=>{
                setNotices(res.data.data.noticeSearchDtoList);
                setLastPage(res.data.data.lastPage);
            })
            .catch((err)=>{
                if(err.response.status === 401){
                    alert('권한이 없습니다.');
                    navigate('/');
                }
                else{
                    alert(err.response.data.message);
                }
            });
    }, [currentPage, isEdited]);

    const handleSearchNotices = () => {
        setSearchKeyword(keyword);
        setCurrentPage(1);
        setIsEdited(!isEdited);
    }

    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='800px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '60%'}}>
                            <div className="admin-teamList-title">공지사항</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                        <div className="admin-teamList-selectbox">
                            <select
                                value={searchType}
                                onChange={(e) => setSearchType(e.target.value)}
                                required>
                                <option value="">---</option>
                                <option value="title">제목</option>
                            </select>
                            <input
                                value={keyword}
                                onChange={(e) => setKeyword(e.target.value)}
                                required
                                onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handleSearchNotices();
                                    }
                                }}
                            >
                            </input>
                            <FaSearch style={{cursor: 'pointer'}}/>
                        </div>
                    </div>
                    <div className="pastTest-container" style={{width: '98%'}}>
                        <div className="pastTest-bot-container">
                            <div className="pastTest-bot-title-container">
                                <p className="pastTest-bot-leftTitle">제목</p>
                                <div className="pastTest-verticalLine"></div>
                                <p className="pastTest-bot-rightTitle"
                                   style={{width: '10%'}}>작성자</p>
                                <div className="pastTest-verticalLine"></div>
                                <p className="pastTest-bot-rightTitle"
                                   style={{width: '10%'}}>등록일</p>
                                <div className="pastTest-verticalLine"></div>
                                <p className="pastTest-bot-rightTitle"
                                   style={{width: '10%'}}>조회수</p>
                            </div>
                            <div className="pastTest-bot-quiz-container">
                                <ul className="pastTest-list">
                                    {notices.map(item => (
                                        <li key={item.id}>
                                            <div className="pastTest-bot-title-container"
                                                 style={{background: 'white', gap: '5px', padding: '0px 20px'}}>
                                                <div className="pastTest-file-container">
                                                    <p className="pastTest-bot-leftTitle"
                                                       onClick={()=>navigate(`/admin/notices/${item.noticeId}`)}
                                                       style={{
                                                           cursor: 'pointer',
                                                           width: '30%',
                                                           textAlign: 'left',
                                                       }}>{item.title}</p>
                                                </div>
                                                <div className="pastTest-bot-rightTitle"
                                                     style={{width: '92%', display: 'flex', flexDirection: 'row'}}>
                                                    <p className="pastTest-bot-rightTitle"
                                                       style={{width: '30%'}}>{item.writer}</p>
                                                    <p className="pastTest-bot-rightTitle"
                                                       style={{width: '40%'}}>{format(new Date(item.createdAt), 'yyyy-MM-dd')}</p>
                                                    <p className="pastTest-bot-rightTitle"
                                                       style={{width: '30%'}}>{item.viewCount}</p>
                                                </div>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div className="noticeManage-buttonBox">
                        <button className="noticeManage-button"
                                onClick={() => {
                                    navigate('/admin/notices/write')
                                }}
                        >글쓰기
                        </button>
                    </div>
                    <div className="pastTest-pagination-container">
                        <div className="pastTest-pagination">
                            {lastPage !== 0 && <Pagination
                                totalPages={lastPage}
                                currentPage={currentPage}
                                onPageChange={setCurrentPage}
                            />}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeManage;