import React from 'react'
import './testQuiz.css'
import '../../styles/styles.css'

const TestQuiz = ({quizTitle, value, onChange}) => {
    const maxLength = 500;
    return (
        <div className="quiz-container">
            <div className="quiz-titlebox">
                <p className="quiz-title-text">{quizTitle}</p>
                <button type="button" className="quiz-title-button">📄</button>
            </div>
            <p className="quiz-info-text">※ 문제 우측의 파일 모양 아이콘을 눌러 다운로드하세요</p>
            <div className="quiz-underline"></div>
            <div className="quiz-mainbox">
                <div className="quiz-file-box">
                    <p className="quiz-normal-text">첨부파일</p>
                    <div className="quiz-vertical-line"></div>
                    <input
                        className="quiz-filename" value={'1조_1번_예선답안.pdf'} readOnly></input>
                    <button type="button" className="quiz-file-button">등록</button>
                </div>
                <div className="quiz-underline" style={{marginTop: '-10px', order:'0'}}></div>
                <div className="quiz-text-box">
                    <div className="quiz-text-upperbox">
                        <p className="quiz-normal-text">텍스트</p>
                        <div className="quiz-vertical-line"></div>
                        <textarea
                            className="quiz-solveText"
                            maxLength={maxLength}
                            value={value}
                            onChange={onChange}
                            placeholder='텍스트로 답을 입력하세요.'
                            wrap="hard"
                        ></textarea>
                    </div>
                    <div className="quiz-text-lowerbox">
                        <p className="quiz-text-lowerinfo">※ 텍스트는 500자 이내로 작성되어야 합니다</p>
                        <p className="quiz-text-limit">
                            {value.length} / {maxLength}자
                        </p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TestQuiz