import './teamAnswerList.css'
import {useEffect, useState} from "react";
import apiClient from "../../../templates/apiClient";
import { format } from 'date-fns';

const TeamAnswerList = ({onClose, teamId, teamName}) => {
    const [answers, setAnswers] = useState([]);
    const [answerDetails, setAnswerDetails] = useState([]);
    useEffect(() => {
        apiClient.get(`/api/admin/v1/teams/${teamId}/team-solves`, {
            params: { team_solve_type: 'submitted' }
        })
            .then((res) => {
                setAnswers(res.data.data.teamSolveListDtos)
            })
            .catch((err) => {});
    }, [teamId])

    useEffect(() => {
        const detailPromises = answers.map((answer) =>
            apiClient.get(`/api/admin/v1/teams/${teamId}/team-solves/${answer.teamSolveId}`, {skipErrorHandler: true})
                .then(res => res.data.data)
        );
        Promise.all(detailPromises)
            .then((allDetails) => {
                setAnswerDetails(allDetails);
            })
            .catch((err) => {
                alert(err.response.data.message);
                setAnswerDetails([]);
            });
    }, [answers]);
    return (
        <div className="teamAnswerList-container">
            <button
                onClick={onClose}
                className="deletedmodal-close-button"
                aria-label="닫기"
            >
                ×
            </button>
            <h2 className="deletedmodal-title">{teamName}팀 답안</h2>
            {answers.length === 0 ? (
                <p className="deletedmodal-empty">제출된 답안이 없습니다.</p>
            ) : (
                answers.map((answer, index) => {
                    const detail = answerDetails[index]; // index로 매핑
                    return (
                        <div key={answer.teamSolveId} className="deletedmodal-item" style={{flexDirection: 'column'}}>
                            <div style={{flexDirection: 'row'}}>
                                <span>{answer.section === 'COMMON' ? '공통' : '수준별'} 문제 답안 </span>
                                <span>({format(new Date(answer.updatedAt), 'yyyy-MM-dd HH:mm:ss')})</span>
                                <div className="quiz-underline"></div>
                            </div>
                            {/* 파일 다운로드 (fileId가 있을 경우) */}
                            {detail?.fileId ? (
                                <div style={{marginTop: '6px'}}>
                                    <p className="teamAnswerList-text">📄{detail.fileName || '파일 다운로드'}</p>
                                </div>
                            ) : (
                                <div style={{marginTop: '6px'}}>
                                    <p className="teamAnswerList-text" style={{color: 'black', cursor: 'none'}}>📄 제출된 파일 없음</p>
                                </div>
                                )}

                            {/* 텍스트 답안 (content) 표시 */}
                            <div style={{marginTop: '6px'}}>
                            <textarea
                                value={detail.content}
                                readOnly
                                style={{
                                    width: '100%',
                                    resize: 'none',
                                    border: '1px solid #ccc',
                                    borderRadius: '4px',
                                    padding: '8px',
                                    backgroundColor: '#f9f9f9',
                                    fontSize: '12px'
                                }}
                                rows={4}
                            />
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );

}

export default TeamAnswerList;