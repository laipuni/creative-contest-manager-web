import React, { useEffect, useState } from 'react';
import './findIdPage.css';
import apiClient from '../../../templates/apiClient';
import SubHeader from "../../../components/subHeader/subHeader";

function FindIdPage() {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState('');
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    const [findedId, setFindedId] = useState('');

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

    return (
        <div className="login-page-container">
            <SubHeader />
            <div className="login-content-container" style={{width: '90%', marginTop: '60px', marginLeft: '150px'}}>
                <div className="login-content-text" style={{width: '500px'}}>
                    <p className="login-title">아이디 찾기</p>
                    <div className="login-body">
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
                            <div className="login-input-field">
                                <p className="login-input-title">아이디</p>
                                <p className="login-input-body" style={{ color: 'black' }}>{findedId}</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default FindIdPage;
