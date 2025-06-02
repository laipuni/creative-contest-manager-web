import React, {useEffect, useState} from 'react';
import './emailVerificationModal.css';
import apiClient from "../../templates/apiClient";

function EmailVerificationModal({ onVerify, baseEmail, isEdit = false }) {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState(''); // 인증 결과 메시지 상태 추가
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    useEffect(() => {
        if(baseEmail){
            onVerify(baseEmail);
            setIsVerified(true);
            setEmailInput(baseEmail);
        }
    }, [baseEmail]);

    useEffect(() => {
        setVerificationMessage('');
        setIsVerificationSent(false);
        setVerificationCode('');
    }, [emailInput])


    const handleSendVerification = () => {
        if(baseEmail && baseEmail === emailInput) {
            onVerify(baseEmail);
            setIsVerified(true)
            alert('기존 이메일과 동일합니다.');
            return;
        }
        setVerificationMessage('');
        setIsVerificationSent(false);
        setVerificationCode('');
        setIsVerified(false);
        onVerify(null);
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;
        if (!emailRegex.test(emailInput)) {
            setVerificationMessage('유효한 이메일 주소를 입력해주세요.');
            return;
        }
        setIsSending(true);
        // 인증 메일 전송 로직 (서버 API 호출)
        if (isEdit === false) {
            apiClient.post('/api/v1/send-auth-code', {
                recipient: emailInput,
                senderType: 'email',
                strategyType: 'register'
            })
                .then((res) => {
                    setIsVerificationSent(true);
                    setVerificationMessage('인증 메일이 전송되었습니다. 메일을 확인해주세요.'); // 성공 메시지 표시
                })
                .catch((err) => {
                })
                .finally(() => {
                    setIsSending(false);
                })
        } else {
            apiClient.post('/api/members/profile/send-update-code', {
                recipient: emailInput,
                senderType: 'email',
                strategyType: 'register'
            })
                .then((res) => {
                    setIsVerificationSent(true);
                    setVerificationMessage('인증 메일이 전송되었습니다.'); // 성공 메시지 표시
                })
                .catch((err) => {
                })
                .finally(() => {
                    setIsSending(false);
                })
        }

    };

    const handleVerify = () => {
        // 인증 코드 확인 로직 (서버 API 호출)
        if (isEdit === false) {
            apiClient.post('/api/verify-register-code', {
                recipient: emailInput,
                authCode: verificationCode,
                strategyType: 'register'
            }, {skipErrorHandler: true})
                .then((res) => {
                    setIsVerified(true);
                    onVerify(emailInput);
                    setIsVerificationSent(false);
                    setVerificationMessage('');
                })
                .catch((err) => {
                    if(err.response.data.message === '유효하지 않은 인증 코드입니다.')
                        setVerificationMessage('인증에 실패했습니다. 인증코드를 다시 확인해주세요.');
                    else alert(err.response.data.message);
                })
        } else {
            apiClient.post('/api/members/profile/verify-update-code', {
                recipient: emailInput,
                authCode: verificationCode,
                strategyType: 'register'
            }, {skipErrorHandler: true})
                .then((res) => {
                    setIsVerified(true);
                    onVerify(emailInput);
                    setIsVerificationSent(false);
                    setVerificationMessage('');
                })
                .catch((err) => {
                    if(err.response.data.message === '유효하지 않은 인증 코드입니다.')
                        setVerificationMessage('인증에 실패했습니다. 인증코드를 다시 확인해주세요.');
                    else alert(err.response.data.message);
                })
        }
    };

    return (
        <>
            <div className="join2-right-row" style={{gap: '5px'}}>
                <input
                    style={{width: '150px'}}
                    className="join2-id-input"
                    type="text"
                    value={emailInput}
                    onChange={(e)=>setEmailInput(e.target.value)}
                    placeholder="이메일 주소를 입력하세요"
                />
                <button className="join2-id-button"
                        type="button"
                        onClick={handleSendVerification}
                        disabled={isSending}
                >
                    {isSending ? (
                    <span className="spinner" />
                ) : (
                    '인증하기'
                )}</button>
                {isVerified === true && <p style={{order: 3}}>✅</p>}
            </div>
                {/* 인증 코드 입력창 (메일 발송 후 노출) */}
                {isVerificationSent && (
                    <div className="join2-right-row" style={{gap: '5px', marginTop: '10px'}}>
                        <input
                            style={{width: '150px'}}
                            type="text"
                            className="join2-id-input"
                            value={verificationCode}
                            onChange={(e) => setVerificationCode(e.target.value)}
                            placeholder="인증 코드를 입력하세요."
                        />
                        <button
                            type="button"
                            className="join2-id-button"
                            onClick={handleVerify}
                            disabled={isSending}
                        >
                            확인
                        </button>
                    </div>
                )}

                {/* 인증 메시지 표시 */}
                {verificationMessage && (
                    <p className="verification-message" style={{marginTop: '8px'}}>
                        {verificationMessage}
                    </p>
                )}
            </>
    );
}

export default EmailVerificationModal;