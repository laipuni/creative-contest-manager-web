import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './schoolSearchModal.css';

const gubunOptions = [
    { label: '초등학교', value: 'elem_list' },
    { label: '중학교', value: 'midd_list' },
    { label: '고등학교', value: 'high_list' },
    { label: '대학교', value: 'univ_list' },
    { label: '특수학교', value: 'seet_list' },
    { label: '기타', value: 'alte_list' },
];

const SchoolSearchModal = ({ isOpen, onClose, onSelectSchool, level }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [resultList, setResultList] = useState([]);
    const [loading, setLoading] = useState(false);
    const [gubun, setGubun] = useState('univ_list');
    const [thisPage, setThisPage] = useState(1);
    const [totalCount, setTotalCount] = useState(0);
    const perPage = 10;

    useEffect(() => {
        if (level) {
            switch (level) {
                case 's_초등학생':
                    setGubun('elem_list');
                    break;
                case 's_중학생':
                    setGubun('midd_list');
                    break;
                case 's_고등학생':
                    setGubun('high_list');
                    break;
                case 's_대학생':
                    setGubun('univ_list');
                    break;
                default:
                    setGubun('alte_list');
                    break;
            }
        }
    }, [level]);

    const handleSearch = async () => {
        setLoading(true);
        try {
            const res = await axios.get('https://www.career.go.kr/cnet/openapi/getOpenApi', {
                params: {
                    apiKey: 'cc5b04f8298db5fdc564d9b1d9dbf8b4',
                    svcType: 'api',
                    svcCode: 'SCHOOL',
                    contentType: 'json',
                    gubun,
                    searchSchulNm: searchTerm,
                    thisPage,
                    perPage,
                },
            });
            const list = res.data.dataSearch?.content || [];
            setResultList(list);
        } catch (error) {
            alert('학교 검색에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleSelect = (school) => {
        onSelectSchool(school);
        onClose();
    };

    const computedTotalPages = Math.ceil(totalCount / perPage);
    const totalPages = computedTotalPages === 0 ? 1 : computedTotalPages;

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
        }
    };

    useEffect(() => {
        if (searchTerm) {
            handleSearch();
        }
    }, [thisPage, gubun]); // thisPage 와 gubun이 변경되면 검색을 실행

    if (!isOpen) return null;

    return (
        <div className="schoolModal-modal-overlay">
            <div className="schoolModal-modal-content">
                <button type="button" className="schoolModal-close-top" onClick={onClose}>
                    &times;
                </button>
                <h2>학교 검색</h2>
                <div className="schoolModal-filter-row">
                    <select value={gubun} onChange={(e) => {setGubun(e.target.value); setThisPage(1);}}>
                        {gubunOptions.map((opt) => (
                            <option key={opt.value} value={opt.value}>
                                {opt.label}
                            </option>
                        ))}
                    </select>
                </div>
                <div className="schoolModal-autocomplete-wrapper">
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="학교명을 입력하세요"
                    />
                    <button type="button" className="schoolModal-search-button" onClick={handleSearch}>
                        🔍
                    </button>
                </div>
                {loading && <p>검색 중...</p>}
                {resultList.length === 0 && !loading && searchTerm && (
                    <p className="schoolModal-no-result">검색 결과가 없습니다.</p>
                )}
                <ul className="schoolModal-result-list">
                    {resultList.map((school, index) => (
                        <li key={index} onClick={() => handleSelect(school)}>
                            <div className="schoolModal-school-name">{school.schoolName}</div>
                            <div className="schoolModal-school-detail">
                                {school.region} | {school.adres} | {school.estType}
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default SchoolSearchModal;