import apiClient from "../../templates/apiClient"
import { format } from 'date-fns';
import {useEffect} from "react";

const ContestAnswerList = ({onClose, answers, teamName, contestId}) => {
    useEffect(() => {
        console.log(teamName);
        console.log(answers);
    }, []);
    //답안 파일 다운로드
    const handleDownloadFile = (fileId, fileName) => {
        apiClient.get(`/api/contests/${contestId}/files/${fileId}`, {
            responseType: 'blob',
            skipErrorHandler: true
        }).then(res => {
            const blob = new Blob([res.data]);
            const fileUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = fileUrl;
            link.download = fileName || "답안파일.pdf";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }).catch(err => {
            alert('답안 파일을 다운로드할 수 없습니다.');
        });
    };
    return (
        <div className="teamAnswerList-container" style={{zIndex: '1001'}}>
            <button
                onClick={onClose}
                className="deletedmodal-close-button"
                aria-label="닫기"
            >
                ×
            </button>
            <h2 className="deletedmodal-title">{teamName}팀 최종제출 답안</h2>
            {answers.length === 0 ? (
                <p className="deletedmodal-empty">제출된 답안이 없습니다.</p>
            ) : (
                answers.map((answer) => {
                    return (
                        <div key={answer.teamSolveId} className="deletedmodal-item" style={{flexDirection: 'column'}}>
                            <div style={{flexDirection: 'row'}}>
                                <span>{answer.section === 'COMMON' ? '공통' : '수준별'} 문제 답안 </span>
                                <span>({format(new Date(answer.updatedAt), 'yyyy-MM-dd HH:mm:ss')})</span>
                                <div className="quiz-underline"></div>
                            </div>
                            {/* 파일 다운로드 (fileId가 있을 경우) */}
                            {answer.fileId ? (
                                <div style={{marginTop: '6px'}}>
                                    <p className="teamAnswerList-text"
                                       onClick={()=>handleDownloadFile(answer.fileId, answer.fileName)}>📄{answer.fileName || '파일 다운로드'}</p>
                                </div>
                            ) : (
                                <div style={{marginTop: '6px'}}>
                                    <p className="teamAnswerList-text" style={{color: 'black', cursor: 'none'}}>📄 제출된 파일 없음</p>
                                </div>
                            )}

                            {/* 텍스트 답안 (content) 표시 */}
                            <div style={{marginTop: '6px'}}>
                            <textarea
                                value={answer?.content}
                                readOnly
                                className="teamAnswerList-textarea"
                                rows={8}
                            />
                            </div>
                        </div>
                    )
                })
            )}
        </div>
    );

}

export default ContestAnswerList;