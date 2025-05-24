import React, {useState} from 'react'
import './testQuiz.css'
import '../../styles/styles.css'
import apiClient from "../../templates/apiClient";
import rocket from "../../styles/images/solve_icon.png";
import PDFPreview from "../pdfPreview/pdfPreview";

const TestQuiz = ({quizTitle, textVal, textOnChange, fileVal, fileOnChange, quiz, contestInfo, answer, teamInfo, setIsPosted}) => {
    const maxLength = 500;
    const inputId = `file-upload-${quiz?.section || quizTitle}`;
    const [showPreview, setShowPreview] = useState(false);
    const [previewUrl, setPreviewUrl] = useState(null);

    //미리보기 토글
    const togglePreview = () => {
        if (!showPreview) {
            apiClient.get(`/api/contests/${contestInfo.contestId}/files/${quiz.fileList[0].fileId}`, {
                responseType: 'blob',
                skipErrorHandler: true
            }).then(res => {
                const blob = new Blob([res.data], { type: 'application/pdf' });
                setPreviewUrl(blob);
            }).catch(err => {
            });
        }
        setShowPreview(!showPreview);
    };
    // //예선 문제 다운로드
    // const handleDownloadProblem = () => {
    //     apiClient.get(`/api/contests/${contestInfo.contestId}/files/${quiz.fileList[0].fileId}`, {
    //         responseType: 'blob',
    //         skipErrorHandler: true
    //     }).then(res => {
    //         const blob = new Blob([res.data]);
    //         const fileUrl = window.URL.createObjectURL(blob);
    //         const link = document.createElement('a');
    //         link.href = fileUrl;
    //         link.download = quiz.title || "문제파일.pdf";
    //         document.body.appendChild(link);
    //         link.click();
    //         document.body.removeChild(link);
    //     }).catch(err => {
    //         alert('문제 파일을 다운로드할 수 없습니다.');
    //     });
    // };

    //제출된 답안 파일 다운로드
    const handleDownloadAnswer = () => {
        apiClient.get(`/api/teams/${teamInfo.teamId}/files/${answer.fileId}/answer/download`, {
            responseType: 'blob',
            skipErrorHandler: true
        }).then(res => {
            const blob = new Blob([res.data]);
            const fileUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = fileUrl;
            link.download = answer.fileName || "답안파일.pdf";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }).catch(err => {
            alert('답안 파일을 다운로드할 수 없습니다.');
        });
    };

    return (
        <div className="quiz-container">
            <div className="quiz-titlebox">
                <p className="quiz-title-text">{quizTitle} 문제</p>
                {quiz && <p onClick={togglePreview} className="quiz-title-button">📄</p>}
            </div>
            <p className="quiz-info-text">※ 문제 우측의 파일 아이콘을 통해 미리보기를 켜거나 끌 수 있습니다</p>
            <div className="quiz-underline"></div>
            <div className="quiz-mainbox">
                <div className="quiz-file-box">
                    <p className="quiz-normal-text">첨부파일</p>
                    <div className="quiz-vertical-line"></div>

                    {/* 조건별 다운로드 표시 */}
                    {!fileVal && !answer.fileId && (
                        <p className="quiz-normal-text" style={{ fontSize: '12px' }}>등록된 파일 없음</p>
                    )}

                    {fileVal && (
                        <a
                            href={URL.createObjectURL(fileVal)}
                            download={fileVal.name}
                            style={{
                                display: 'inline-block',
                                width: '15%',
                                overflow: 'hidden',
                                whiteSpace: 'nowrap',
                                textOverflow: 'ellipsis',
                                color: '#000000',
                                fontFamily: 'Roboto',
                                fontWeight: 400,
                                fontSize: '12px',
                            }}
                        >
                            {fileVal.name}
                        </a>
                    )}

                    {!fileVal && answer.fileId && (
                        <a onClick={handleDownloadAnswer}
                           style={{
                               cursor: 'pointer',
                               borderBottom: '1px solid black',
                               display: 'inline-block',
                               width: '15%',
                               overflow: 'hidden',
                               whiteSpace: 'nowrap',
                               textOverflow: 'ellipsis',
                               color: '#000000',
                               fontFamily: 'Roboto',
                               fontWeight: 400,
                               fontSize: '12px',
                           }}>
                            {answer.fileName || '답안 다운로드'}
                        </a>
                    )}

                    <input
                        type="file"
                        accept=".pdf"
                        onChange={fileOnChange}
                        className="quiz-filename" id={inputId}
                        style={{ display: "none" }}
                    />
                    <label htmlFor={inputId} className="quiz-file-button">
                        파일 등록
                    </label>
                </div>

                <div className="quiz-underline" style={{ marginTop: '-10px', order: '0' }}></div>

                <div className="quiz-text-box">
                    <div className="quiz-text-upperbox">
                        <p className="quiz-normal-text">텍스트</p>
                        <div className="quiz-vertical-line"></div>
                        <textarea
                            className="quiz-solveText"
                            maxLength={maxLength}
                            value={textVal}
                            onChange={textOnChange}
                            placeholder='텍스트로 답을 입력하세요.'
                            wrap="hard"
                        ></textarea>
                    </div>
                    <div className="quiz-text-lowerbox">
                        <p className="quiz-text-lowerinfo">※ 텍스트는 500자 이내로 작성되어야 합니다</p>
                        <p className="quiz-text-limit">
                            {textVal.length} / {maxLength}자
                        </p>
                    </div>
                </div>
            </div>
            {/* 미리보기 표시 */}
            {showPreview && previewUrl && (
                <div className="pdf-preview">
                    <PDFPreview blob={previewUrl} />
                </div>
            )}
        </div>
    );
};


export default TestQuiz