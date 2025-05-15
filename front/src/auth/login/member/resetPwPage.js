import React, { useEffect, useState } from 'react';
import SubHeader from '../../../components/subHeader/subHeader'
import apiClient from "../../../templates/apiClient";

function ResetPasswordPage() {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState('');
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    const [loginId, setLoginId] = useState('');
    const [session, setSession] = useState('');
    const [resetPassword, setResetPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    useEffect(() => {
        setVerificationMessage('');
        setIsVerificationSent(false);
    }, [emailInput, loginId]);

    const handleSendVerification = () => {
        setVerificationMessage('');
        setIsVerified(false);

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;
        if (!emailRegex.test(emailInput)) {
            setVerificationMessage('유효한 이메일 주소를 입력해주세요.');
            return;
        }

        setIsSending(true);
        apiClient.post('/api/password-reset/request', {
            recipient: emailInput,
            senderType: 'email',
            loginId
        }).then(() => {
            setIsVerificationSent(true);
            setVerificationMessage('인증 메일이 전송되었습니다.');
        }).catch(() => {
            setVerificationMessage('메일 전송에 실패했습니다.');
        }).finally(() => {
            setIsSending(false);
        });
    };

    const handleVerify = () => {
        apiClient.post('/api/password-reset/confirm', {
            recipient: emailInput,
            authCode: verificationCode,
            loginId,
            senderType: 'email'
        }, { skipErrorHandler: true })
            .then((res) => {
                setSession(res.data.data.session);
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(true);
            }).catch((err) => {
            setVerificationMessage(err.response?.data?.message || '인증에 실패했습니다.');
        });
    };

    const handlePasswordReset = () => {
        apiClient.post('/api/password-reset', {
            session,
            resetPassword,
            confirmPassword,
            loginId
        }, { skipErrorHandler: true })
            .then(() => {
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(false);
                alert('비밀번호 변경이 완료되었습니다.\n새로운 비밀번호로 로그인 해주세요.');
                window.location.href = '/member/login'; // 비밀번호 변경 후 로그인 페이지로 이동
            }).catch((err) => {
            setVerificationMessage(err.response?.data?.message || '비밀번호 변경 실패');
        });
    };

    return (
        <div className="login-page-container">
            <SubHeader />
            <div className="login-content-container" style={{width: '90%', marginTop: '60px', marginLeft: '150px'}}>
                <div className="login-content-text" style={{width: '500px'}}>
                    <p className="login-title">비밀번호 재설정</p>
                    <div className="login-body">
                        {!isVerified && <>
                            <input
                                type="text"
                                className="login-input-body"
                                value={loginId}
                                onChange={(e) => setLoginId(e.target.value)}
                                placeholder="아이디를 입력하세요."
                                disabled={isVerified}
                            />
                            <div className="login-input-field">
                                <input
                                    type="text"
                                    className="login-input-body"
                                    value={emailInput}
                                    onChange={(e) => setEmailInput(e.target.value)}
                                    placeholder="이메일 주소를 입력하세요."
                                    disabled={isVerified}
                                />
                                <button
                                    type="button"
                                    className="login-button"
                                    onClick={handleSendVerification}
                                    disabled={isSending}
                                >
                                    {isSending ? '전송 중...' : '인증 메일 받기'}
                                </button>
                            </div>
                        </>}

                        {isVerificationSent && (
                            <>
                                <input
                                    type="text"
                                    className="login-input-body"
                                    value={verificationCode}
                                    onChange={(e) => setVerificationCode(e.target.value)}
                                    placeholder="인증 코드를 입력하세요."
                                />
                                <button
                                    type="button"
                                    className="login-button"
                                    onClick={handleVerify}
                                >
                                    인증
                                </button>
                            </>
                        )}

                        {verificationMessage && (
                            <p className="verification-message">{verificationMessage}</p>
                        )}

                        {isVerified && (
                            <>
                                <input
                                    type="password"
                                    className="login-input-body"
                                    value={resetPassword}
                                    onChange={(e) => setResetPassword(e.target.value)}
                                    placeholder="새 비밀번호를 입력하세요."
                                />
                                <input
                                    type="password"
                                    className="login-input-body"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="비밀번호 확인"
                                />
                                <button
                                    type="button"
                                    className="login-button"
                                    onClick={handlePasswordReset}
                                >
                                    비밀번호 변경
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ResetPasswordPage;
