import './teamAnswerList.css'
import {useEffect, useState} from "react";
import apiClient from "../../../templates/apiClient";
import { format } from 'date-fns';

const TeamAnswerList = ({onClose, teamId, teamName}) => {
    const [answers, setAnswers] = useState([]);
    useEffect(() => {
        apiClient.get(`/api/admin/v1/teams/${teamId}/team-solves`, {
            params: { team_solve_type: 'submitted' }
        })
            .then((res) => {
                setAnswers(res.data.data.teamSolveListDtos)
            })
            .catch((err) => {});
    }, [teamId])
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
                answers.map(answer => (
                    <div key={answer.teamSolveId}
                         className="deletedmodal-item">
                        <span>{answer.section === 'COMMON' ? '공통' : '수준별'} 문제 </span>
                        <span>{format(new Date(answer.updatedAt), 'yyyy-MM-dd HH:mm:ss')}</span>
                    </div>
                ))
            )}
        </div>
    )

}

export default TeamAnswerList;