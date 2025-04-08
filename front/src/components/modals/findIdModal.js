import React, {useEffect, useState} from 'react';
import './emailVerificationModal.css';
import apiClient from "../../templates/apiClient";

function FindIdModal({ onClose }) {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState(''); // 인증 결과 메시지 상태 추가
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    const [findedId, setFindedId] = useState('');

    useEffect(() => {
        setVerificationMessage('');
    }, [emailInput])

    const handleSendVerification = () => {
        setVerificationMessage('');
        setIsVerified(false);
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;
        if (!emailRegex.test(emailInput)) {
            setVerificationMessage('유효한 이메일 주소를 입력해주세요.');
            return;
        }
        setIsSending(true);
        // 인증 메일 전송 로직 (서버 API 호출)
        apiClient.post('/api/v1/send-auth-code', {recipient: emailInput, senderType: 'email', strategyType: 'findId'})
            .then((res) => {
                setIsVerificationSent(true);
                setVerificationMessage('인증 메일이 전송되었습니다.'); // 성공 메시지 표시
            })
            .catch((err) => {})
            .finally(()=>{
                setIsSending(false);
            })

    };

    const handleVerify = () => {
        // 인증 코드 확인 로직 (서버 API 호출)
        apiClient.post('/api/v1/find-id', {recipient: emailInput, authCode: verificationCode, senderType: 'email'},
            {skipErrorHandler: true})
            .then((res)=>{
                setFindedId(res.data.data.loginId)
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(true);
            })
            .catch((err)=>{
                setVerificationMessage(err.response.data.message);
            })
    };

    function handleClose() {
        setFindedId('');
        onClose();
    }
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
                    <button type="button" className="email-button"
                            onClick={handleSendVerification} disabled={isSending}>{isSending ? (
                        <span className="spinner" />
                    ) : (
                        '인증 메일 받기'
                    )}</button>
                </div>
                {isVerificationSent && (
                    <div className="email-inner-container" style={{background: 'lightgray', borderRadius: '10px', width: '300px', height: '50px', padding: '10px 30px'}}>
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
                {isVerified && (
                    <div className="email-inner-container" style={{background: 'lightgray', borderRadius: '10px', width: '300px', height: '50px', padding: '10px 30px'}}>
                        <p className="verification-message" style={{color: "black"}}>아이디 : {findedId}</p>
                        <button type="button" className="email-button" onClick={handleClose}>확인</button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default FindIdModal;