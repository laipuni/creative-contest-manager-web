import React, {useEffect, useState} from 'react';
import '../../../components/modals/emailVerificationModal.css';
import apiClient from "../../../templates/apiClient";

function FindIdModal({ onClose }) {
    const [emailInput, setEmailInput] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerificationSent, setIsVerificationSent] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState(''); // 인증 결과 메시지 상태 추가
    const [isSending, setIsSending] = useState(false);
    const [isVerified, setIsVerified] = useState(false);
    const [loginId, setLoginId] = useState('');
    const [session, setSession] = useState('');
    const [resetPassword, setResetPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    useEffect(() => {
        setVerificationMessage('');
        setIsVerificationSent(false);
    }, [emailInput, loginId])

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
        apiClient.post('/api/password-reset/request', {recipient: emailInput, senderType: 'email', loginId})
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
        apiClient.post('/api/password-reset/confirm', {recipient: emailInput, authCode: verificationCode,
                loginId, senderType: 'email'},{skipErrorHandler: true})
            .then((res)=>{
                setSession(res.data.data.session)
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(true);
            })
            .catch((err)=>{
                setVerificationMessage(err.response.data.message);
            })
    };

    const handlePasswordReset = () => {
        apiClient.post('/api/password-reset', {session, resetPassword, confirmPassword, loginId},
            {skipErrorHandler: true})
            .then((res)=>{
                setVerificationMessage('');
                setIsVerificationSent(false);
                setIsVerified(false);
                onClose();
                alert('비밀번호 변경이 완료되었습니다.\n새로운 비밀번호로 로그인 해주세요.');
            })
            .catch((err)=>{
                setVerificationMessage(err.response.data.message);
            })
    }

    return (
        <div className="email-modal">
            <span className="email-modal-close" onClick={onClose}>&times;</span>
            <h2>이메일 인증</h2>
            <div className="email-modal-content">
                <div className="modal-inner-container-vertical">
                    {/* 아이디 입력 - 세로 */}
                    <input
                        type="text"
                        style={{width:'60%'}}
                        className="email-input"
                        value={loginId}
                        onChange={(e) => setLoginId(e.target.value)}
                        placeholder="아이디를 입력하세요."
                        disabled={isVerified}
                    />

                    {/* 이메일 + 버튼 - 가로 */}
                    <div className="modal-horizontal-group">
                        <input
                            type="text"
                            className="email-input"
                            value={emailInput}
                            onChange={(e) => setEmailInput(e.target.value)}
                            placeholder="이메일 주소를 입력하세요."
                            disabled={isVerified}
                        />
                        <button type="button" className="email-button"
                                onClick={handleSendVerification} disabled={isSending}>
                            {isSending ? <span className="spinner"/> : '인증 메일 받기'}
                        </button>
                    </div>
                </div>
                {isVerificationSent && (
                    <div className="modal-verify-vertical">
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
                    <div className="modal-inner-container-vertical" style={{
                        background: 'lightgray',
                        borderRadius: '10px',
                        width: '300px',
                        padding: '20px 30px'
                    }}>
                        <input
                            type="password"
                            className="email-input"
                            value={resetPassword}
                            onChange={(e) => setResetPassword(e.target.value)}
                            placeholder="새로운 비밀번호를 입력하세요."
                        />
                        <input
                            type="password"
                            className="email-input"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="비밀번호 확인"
                        />
                        <button type="button" className="email-button" onClick={handlePasswordReset}>변경</button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default FindIdModal;