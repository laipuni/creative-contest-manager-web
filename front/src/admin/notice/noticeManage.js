import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import {FaSearch} from "react-icons/fa";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";

const NoticeManage = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [lastPage, setLastPage] = useState(0);
    const [searchType, setSearchType] = useState('loginId');
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [members, setMembers] = useState([]);
    const [isEdited, setIsEdited] = useState(false);
    const [gender, setGender] = useState('');
    const [memberDetails, setMemberDetails] = useState([]);
    const [openMemberId, setOpenMemberId] = useState(null);
    const navigate = useNavigate();

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
                                        //handleSearchNotices();
                                    }
                                }}
                            >
                            </input>
                            <FaSearch style={{cursor: 'pointer'}}/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeManage;