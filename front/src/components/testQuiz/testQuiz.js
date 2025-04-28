import React from 'react'
import './testQuiz.css'
import '../../styles/styles.css'

const TestQuiz = ({quizTitle, textVal, textOnChange, fileVal, fileOnChange, quiz}) => {
    const maxLength = 500;
    return (
        <div className="quiz-container">
            <div className="quiz-titlebox">
                <p className="quiz-title-text">{quizTitle}</p>
                {quiz &&
                <a href={URL.createObjectURL(quiz)}
                   download={quiz.name} className="quiz-title-button">📄</a>}
            </div>
            <p className="quiz-info-text">※ 문제 우측의 파일 모양 아이콘을 눌러 다운로드하세요</p>
            <div className="quiz-underline"></div>
            <div className="quiz-mainbox">
                <div className="quiz-file-box">
                    <p className="quiz-normal-text">첨부파일</p>
                    <div className="quiz-vertical-line"></div>
                    {!fileVal &&
                        <p className="quiz-normal-text" style={{fontSize: '12px'}}>등록된 파일 없음</p>}
                    {fileVal && (
                        <>
                            <a
                                href={URL.createObjectURL(fileVal)}
                                download={fileVal.name}
                                style={{ display: 'inline-block',
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
                        </>
                    )}
                    <input
                        type="file"
                        accept=".pdf"
                        onChange={fileOnChange}
                        className="quiz-filename"id="file-upload"
                        style={{ display: "none" }}
                    />
                    {/* 사용자에게 보일 버튼 */}
                    <label htmlFor="file-upload" className="quiz-file-button">
                        파일 등록
                    </label>
                </div>
                <div className="quiz-underline" style={{marginTop: '-10px', order:'0'}}></div>
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
        </div>
    )
}

export default TestQuiz