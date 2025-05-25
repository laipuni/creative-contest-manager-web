// src/components/DeletedContestList.jsx
import { useEffect, useState } from 'react';
import apiClient from '../../../templates/apiClient';
import './deletedContestList.css';

export default function DeletedContestList({ onRestore, onHardDelete, onClose }) {
    const [deletedContests, setDeletedContests] = useState([]);
    const [isEdited, setIsEdited] = useState(false);

    useEffect(() => {
        apiClient.get('/api/admin/contests/deleted')
            .then((res) => {
                setDeletedContests(res.data.data.deletedContestList || []);
            })
            .catch((err) => {
            });
    }, []);

    const handleDeleteClick = (contestId) => {
        onHardDelete(contestId).then(() => {
            setDeletedContests((prev) => prev.filter(c => c.contestId !== contestId));
        })
            .catch((err) => {alert(err.response.data.message)});
    };

    const handleRestoreClick = (contestId) => {
        onRestore(contestId).then(() => {
            setDeletedContests((prev) => prev.filter(c => c.contestId !== contestId));
        })
            .catch((err) => {alert(err.response.data.message)});
    };


    return (
        <div className="deletedmodal-container">
            <button
                onClick={onClose}
                className="deletedmodal-close-button"
                aria-label="닫기"
            >
                ×
            </button>

            <h2 className="deletedmodal-title">삭제된 대회 목록</h2>

            {deletedContests.length === 0 ? (
                <p className="deletedmodal-empty">삭제된 대회가 없습니다.</p>
            ) : (
                <div className="deletedmodal-list">
                    {deletedContests.map((contest) => (
                        <div
                            key={contest.contestId}
                            className="deletedmodal-item"
                        >
                            <span>{contest.season}회 |</span>
                            <div className="deletedmodal-buttons">
                                <button
                                    onClick={() => {
                                        setIsEdited(!isEdited);
                                        handleRestoreClick(contest.contestId)}}
                                    className="deletedmodal-restore"
                                >
                                    복구
                                </button>
                                <button
                                    onClick={() => {
                                        setIsEdited(!isEdited);
                                        handleDeleteClick(contest.contestId)}}
                                    className="deletedmodal-delete"
                                >
                                    영구 삭제
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
