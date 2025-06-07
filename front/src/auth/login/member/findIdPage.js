import React, { useEffect, useState } from 'react';
import './findIdPage.css';
import apiClient from '../../../templates/apiClient';
import SubHeader from "../../../components/subHeader/subHeader";
import { Link } from 'react-router-dom'
import { MdContentCopy } from "react-icons/md";

function FindIdPage() {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState('');
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    const [findedId, setFindedId] = useState('');
    const [copied, setCopied] = useState(false);

    useEffect(() => {
        setVerificationMessage('');
        setIsVerificationSent(false);
        setVerificationCode('');
    }, [emailInput]);

    const handleSendVerification = () => {
        setVerificationMessage('');
        setIsVerified(false);
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;
        if (!emailRegex.test(emailInput)) {
            setVerificationMessage('유효한 이메일 주소를 입력해주세요.');
            return;
        }

        setIsSending(true);
        apiClient.post('/api/v1/send-auth-code', {
            recipient: emailInput,
            senderType: 'email',
            strategyType: 'findId',
        })
            .then(() => {
                setIsVerificationSent(true);
                setVerificationMessage('인증 메일이 전송되었습니다.');
            })
            .catch(() => {})
            .finally(() => {
                setIsSending(false);
            });
    };

    const handleVerify = () => {
        apiClient.post('/api/v1/find-id', {
            recipient: emailInput,
            authCode: verificationCode,
            senderType: 'email',
        }, { skipErrorHandler: true })
            .then((res) => {
                setFindedId(res.data.data.loginId);
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(true);
            })
            .catch((err) => {
                setVerificationMessage(err.response.data.message);
            });
    };

    const handleCopy = () => {
        navigator.clipboard.writeText(findedId)
            .then(() => {
                setCopied(true);
                setTimeout(() => setCopied(false), 1500); // 1.5초 뒤 복사 알림 사라짐
            })
            .catch(err => {
                console.error("복사 실패:", err);
            });
    };

    return (
        <div className="login-page-container">
            <SubHeader />
            <div className="login-content-container" style={{width: '90%', marginTop: '60px', marginLeft: '150px'}}>
                <div className="login-content-text" style={{width: '500px'}}>
                    <p className="login-title">아이디 찾기</p>
                    <div className="login-body">
                        {!isVerified &&
                            <>
                                <div className="login-input-field">
                                    <p className="login-input-title">이메일</p>
                                    <input
                                        className="login-input-body"
                                        type="text"
                                        value={emailInput}
                                        onChange={(e) => setEmailInput(e.target.value)}
                                        placeholder="이메일 주소를 입력하세요."
                                        disabled={isVerified}
                                    />
                                </div>
                                <button
                                    type="button"
                                    className="login-button"
                                    onClick={handleSendVerification}
                                    disabled={isSending}
                                >
                                    {isSending ? '전송 중...' : '인증 메일 받기'}
                                </button>
                        </>
                        }

                        {isVerificationSent && (
                            <>
                                <div className="login-input-field">
                                    <p className="login-input-title">인증 코드</p>
                                    <input
                                        className="login-input-body"
                                        type="text"
                                        value={verificationCode}
                                        onChange={(e) => setVerificationCode(e.target.value)}
                                        placeholder="인증 코드를 입력하세요."
                                    />
                                </div>
                                <button className="login-button" onClick={handleVerify}>
                                    인증
                                </button>
                            </>
                        )}

                        {verificationMessage && (
                            <p className="verification-message">{verificationMessage}</p>
                        )}

                        {isVerified && (
                            <>
                                <div className="login-input-field">
                                    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                                        <p style={{ background: 'white', color: 'black', margin: 0 }}>
                                            회원님의 아이디는 <span style={{color: 'blue'}}>{findedId}</span>입니다
                                        </p>
                                        <MdContentCopy
                                            onClick={handleCopy}
                                            size={20}
                                            style={{ cursor: "pointer", color: "#555" }}
                                            title="클립보드에 복사"
                                        />
                                        {copied && <span style={{ color: "green", fontSize: "0.9em" }}>복사됨!</span>}
                                    </div>
                                </div>
                                <Link to="/member/login"
                                      className="login-button"
                                      style={{marginTop: '0px', textDecoration: 'none'}}
                                >확인
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default FindIdPage;
