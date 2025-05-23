import React, {useEffect, useState} from "react";
import apiClient from "../../templates/apiClient";
import {useNavigate} from "react-router-dom";
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import trophyLogo from "../../styles/images/test_info_logo.png";
import {format} from "date-fns";
import {FaSearch} from "react-icons/fa";

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

const NoticeList = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [lastPage, setLastPage] = useState(0);
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [notices, setNotices] = useState([]);
    const [searchType, setSearchType] = useState(null);
    const [isEdited, setIsEdited] = useState(false);


    useEffect(() => {
        const params = searchKeyword
            ? { page: currentPage - 1, keyword: searchKeyword, search_type: searchType }
            : { page: currentPage - 1 };
        apiClient.get('/api/notices/search', {params})
            .then((res)=>{
                setNotices(res.data.data.noticeSearchDtoList);
                setLastPage(res.data.data.lastPage);
            })
            .catch((err)=>{
            });
    }, [currentPage, isEdited]);

    const handleSearchNotices = () => {
        setSearchKeyword(keyword);
        setCurrentPage(1);
        setIsEdited(!isEdited);
    }

    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"공지사항"} imgSrc={trophyLogo} imageWidth='18%'/>
                        <div className="pastTest-container">
                            <div className="pastTest-top-container">
                                <div className="admin-teamList-selectbox" style={{width: '100%', justifyContent: 'flex-end'}}>
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
                                <div className="pastTest-top-underline"></div>
                            </div>
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
                                                           style={{
                                                               width: '30%',
                                                               textAlign: 'left',
                                                           }}>{item.title}</p>
                                                    </div>
                                                    <div className="pastTest-bot-rightTitle"
                                                         style={{width: '100%', display: 'flex', flexDirection: 'row'}}>
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
                </div>
            </div>
        </div>
    )
}

export default NoticeList;