import React, {useState} from 'react';
import MainHeader from "../../components/mainHeader/mainHeader";
import Sidebar from "../../components/sidebar/sidebar";
import CategoryLogo from "../../components/categoryLogo/categoryLogo";
import testLogo from "../../styles/images/solve_logo.png";
import './pastTest.css'
import '../../styles/pagination.css'

// 예시 데이터: 파일명, 다운로드 URL, 작성 날짜 포함
const exampleData = Array.from({ length: 23 }, (_, i) => ({
    id: i + 1,
    fileName: `file_${i + 1}.pdf`,
    fileUrl: `/downloads/file_${i + 1}.pdf`,
    date: `2025-04-${String((i % 30) + 1).padStart(2, '0')}`
}));

const ITEMS_PER_PAGE = 10;

// Pagination 컴포넌트
function Pagination({ totalPages, currentPage, onPageChange }) {
    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    return (
        <div className="pagination">
            <button
                disabled={currentPage === 1}
                onClick={() => onPageChange(1)}
            >
                «
            </button>
            <button
                disabled={currentPage === 1}
                onClick={() => onPageChange(currentPage - 1)}
            >
                ‹
            </button>

            {pages.map(page => (
                <button
                    key={page}
                    className={page === currentPage ? 'active' : ''}
                    onClick={() => onPageChange(page)}
                >
                    {page}
                </button>
            ))}

            <button
                disabled={currentPage === totalPages}
                onClick={() => onPageChange(currentPage + 1)}
            >
                ›
            </button>
            <button
                disabled={currentPage === totalPages}
                onClick={() => onPageChange(totalPages)}
            >
                »
            </button>
        </div>
    );
}

const PastTest = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const totalPages = Math.ceil(exampleData.length / ITEMS_PER_PAGE);

    const startIdx = (currentPage - 1) * ITEMS_PER_PAGE;
    const currentData = exampleData.slice(startIdx, startIdx + ITEMS_PER_PAGE);
    return (
        <div className="testInfo-page-container">
            <div className="testInfo-page-inner-container">
                <MainHeader underbarWidth="95%"/>
                <div className="testInfo-content-container">
                    <Sidebar/>
                    <div className="testInfo-main-container">
                        <CategoryLogo logoTitle={"기출문제 풀기"} imgSrc={testLogo} imageWidth='18%'
                                      backgroundColor={'linear-gradient(90deg, #FF6200 0%, #FDEB85 100%)'}/>
                        <div className="pastTest-container">
                            <div className="pastTest-top-container">
                                <div className="pastTest-top-category-container">
                                    <div className="pastTest-top-category">초/중등</div>
                                    <div className="pastTest-top-category">고등/일반</div>
                                    <div className="pastTest-top-category">공통</div>
                                </div>
                                <div className="pastTest-top-underline"></div>
                            </div>
                            <div className="pastTest-bot-container">
                                <div className="pastTest-bot-title-container">
                                    <p className="pastTest-bot-leftTitle">파일</p>
                                    <div className="pastTest-verticalLine"></div>
                                    <p className="pastTest-bot-rightTitle">작성일</p>
                                </div>
                                <div className="pastTest-bot-quiz-container">
                                    <ul className="pastTest-list">
                                        {currentData.map(item => (
                                            <li key={item.id}>
                                                <div className="pastTest-bot-title-container"
                                                     style={{background: 'white', gap: '5px', padding: '0px 20px'}}>
                                                    <div className="pastTest-file-container">
                                                        <p className="pastTest-bot-leftTitle"
                                                           style={{textAlign: 'left', width: 'fit-content'}}>{item.fileName}</p>
                                                        <a className="pastTest-bot-file"
                                                           style={{textDecoration: 'none'}}
                                                           href={item.fileUrl} download>📄</a>
                                                    </div>
                                                    <p className="pastTest-bot-rightTitle"
                                                       style={{width: '70%', textAlign: 'center'}}>{item.date}</p>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                                <div className="pastTest-pagination-container">
                                    <div className="pastTest-pagination">
                                        <Pagination
                                            totalPages={totalPages}
                                            currentPage={currentPage}
                                            onPageChange={setCurrentPage}
                                        />
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
export default PastTest;