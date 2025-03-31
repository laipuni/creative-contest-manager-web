import React, { useState } from 'react';
import './emailVerificationModal.css';

function EmailVerificationModal({ onClose, onVerify }) {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState(''); // 인증 결과 메시지 상태 추가

    const handleSendVerification = () => {
        const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[A-Za-z]+$/;
        if (!emailRegex.test(emailInput)) {
            setVerificationMessage('유효한 이메일 주소를 입력해주세요.');
            return;
        }

        // 인증 메일 전송 로직 (서버 API 호출)
        setIsVerificationSent(true);
        setVerificationMessage('인증 메일이 전송되었습니다.'); // 성공 메시지 표시
    };

    const handleVerify = () => {
        // 인증 코드 확인 로직 (서버 API 호출)

        // 인증 성공/실패 가정 (실제로는 서버 응답에 따라 처리)
        const isVerified = true; // 성공 가정

        if (isVerified) {
            onVerify(emailInput);
            onClose();
        } else {
            setVerificationMessage('인증에 실패했습니다. 이메일을 다시 확인해주세요.');
        }
    };

    return (
        <div className="email-modal">
            <span className="email-modal-close" onClick={onClose}>&times;</span>
            <h2>이메일 인증</h2>
            <div className="email-modal-content">
                <div className="email-inner-container">
                    <input
                        type="text"
                        className="email-input"
                        value={emailInput}
                        onChange={(e) => setEmailInput(e.target.value)}
                        placeholder="이메일 주소를 입력하세요."
                    />
                    <button type="button" className="email-button" onClick={handleSendVerification}>인증 메일 받기</button>
                </div>
                {isVerificationSent && (
                    <div className="email-inner-container" style={{background: 'lightgray', borderRadius: '10px', height: '100px', padding: '5px'}}>
                        <input
                            type="text"
                            className="email-input"
                            value={verificationCode}
                            onChange={(e) => setVerificationCode(e.target.value)}
                            placeholder="인증 코드를 입력하세요."
                        />
                        <button type="button" className="email-button" onClick={handleVerify}>인증</button>
                    </div>
                )}
                {verificationMessage && <p className="verification-message">{verificationMessage}</p>}
            </div>
        </div>
    );
}

export default EmailVerificationModal;