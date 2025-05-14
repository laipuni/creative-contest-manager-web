import React, {useEffect, useState} from 'react'
import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import "../../styles/pagination.css"
import apiClient from "../../templates/apiClient";
import { FaSearch } from 'react-icons/fa';


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

const CertificateManage = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [certificates, setCertificates] = useState([]);
    const [lastPage, setLastPage] = useState(0);
    const [searchType, setSearchType] = useState('loginId');
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [edited, setEdited] = useState(false);

    //증명서 목록 불러오기
    useEffect(() => {
        const params = searchKeyword
            ? { page: currentPage - 1, keyword: searchKeyword, search_type: searchType }
            : { page: currentPage - 1 };

        apiClient.get('/api/admin/certificates/search', { params })
            .then((res) => {
                setCertificates(res.data.data.certificateDtoList);
                setLastPage(res.data.data.lastPage);
            });
    }, [currentPage, edited]);


    const handleSearchCertificates = () => {
        setSearchKeyword(keyword);
        setCurrentPage(1);
        setEdited(!edited);
    }

    const handleDeleteCertificate = (certificateId) => {
        apiClient.delete('/api/admin/certificates', {data: certificateId})
            .then((res) => {
                alert('증명서 삭제 완료');
                setEdited(!edited);
            })
            .catch((err) => {

            })
    }

    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='800px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '70%'}}>
                            <div className="admin-teamList-title">증명서 목록</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                        <div className="admin-teamList-selectbox">
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
                                onKeyDown={(e)=>{if(e.key === 'Enter'){handleSearchCertificates()}}}
                            >
                            </input>
                            <FaSearch style={{cursor: 'pointer'}} onClick={handleSearchCertificates}/>
                        </div>
                    </div>
                    <div className="admin-teamList-body">
                        <div className="admin-teamList-body-title" style={{backgroundColor: 'darkgray'}}>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>회차</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>팀 이름</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>증명서 유형</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>유저 id</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>유저 이름</p>
                            </div>
                            <div className="admin-teamList-body-verticalLine"></div>
                            <div className="admin-teamList-body-title-textbox">
                                <p className="admin-teamList-body-title-text" style={{fontWeight: 'bold'}}>삭제</p>
                            </div>
                        </div>
                        {certificates.map((cert) => (
                            <div
                                key={cert.certificateId}
                                className="admin-teamList-body-title"
                                style={{
                                    backgroundColor: cert.certificateType === 'FINAL' ? 'rgba(121, 30, 182, 0.12)' : 'white'
                                }}
                            >
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{cert.season}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{cert.teamName}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{cert.certificateType === 'FINAL' ? '본선' : '예선'}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{cert.loginId}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox">
                                    <p className="admin-teamList-body-title-text">{cert.name}</p>
                                </div>
                                <div className="admin-teamList-body-verticalLine"></div>
                                <div className="admin-teamList-body-title-textbox" style={{justifyContent: 'center'}}>
                                    <button className="admin-teamList-body-title-text"
                                            onClick={() => {
                                                handleDeleteCertificate(cert.certificateId)
                                            }}
                                            style={{width: 'fit-content', height: "fit-content", padding: '10px 5px'}}>X
                                    </button>
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

export default CertificateManage;