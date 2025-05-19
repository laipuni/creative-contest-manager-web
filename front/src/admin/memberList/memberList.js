import React, {useEffect, useState} from "react";
import apiClient from "../../templates/apiClient";
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import {FaSearch} from "react-icons/fa";
import {useNavigate} from "react-router-dom";

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

const MemberList = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [lastPage, setLastPage] = useState(0);
    const [searchType, setSearchType] = useState('loginId');
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [members, setMembers] = useState([]);
    const [isEdited, setIsEdited] = useState(false);
    const [gender, setGender] = useState('');
    const navigate = useNavigate();
    //유저 목록 불러오기
    useEffect(() => {
        const params = searchKeyword
            ? { page: currentPage - 1, keyword: searchKeyword, search_type: searchType, gender }
            : { page: currentPage - 1, gender };

        apiClient.get('/api/admin/v1/members', { params , skipErrorHandler: true })
            .then((res) => {
                setMembers(res.data.data.memberInfos);
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

    const handleSearchMembers = () => {
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
                            <div className="admin-teamList-title">회원 목록</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                        <div className="admin-teamList-selectbox">
                            <select
                                value={gender}
                                onChange={(e) => setGender(e.target.value)}
                                required>
                                <option value="">---</option>
                                <option value="남자">남자</option>
                                <option value="여자">여자</option>
                            </select>
                            <select
                                value={searchType}
                                onChange={(e) => setSearchType(e.target.value)}
                                required>
                                <option value="loginId">아이디</option>
                                <option value="name">이름</option>
                            </select>
                            <input
                                value={keyword}
                                onChange={(e) => setKeyword(e.target.value)}
                                required
                                onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handleSearchMembers();
                                    }
                                }}
                            >
                            </input>
                            <FaSearch style={{cursor: 'pointer'}}/>
                        </div>
                    </div>
                    <div className="admin-teamList-body">
                        <div className="admin-teamList-body-title" style={{backgroundColor: 'darkgray'}}>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>아이디</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>이름</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>생일</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>성별</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>직업(학교,소속)</p>
                            </div>
                        </div>
                        {members.map((member) => (
                            <div
                                key={member.loginId}
                                className="admin-teamList-body-title"
                            >
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{member.loginId}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{member.name}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{member.birth}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{member.gender === 'MAN' ? '남자' : '여자'}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{member.organization.organizationType}({member.organization.name})</p>
                                </div>
                            </div>
                        ))}

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

export default MemberList;