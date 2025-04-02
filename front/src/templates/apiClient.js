import axios from 'axios';

// CSRF 토큰을 쿠키에서 가져오는 함수
function getCsrfToken() {
    const cookies = document.cookie.split('; ');
    for (const cookie of cookies) {
        const [name, value] = cookie.split('=');
        if (name === 'XSRF-TOKEN') {
            return decodeURIComponent(value);
        }
    }
    return null;
}

// Axios 기본 설정
const apiClient = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true, // 쿠키 기반 인증을 사용하려면 필요
    headers: {
        'Content-Type': 'application/json'
    }
});

// 요청 인터셉터: 모든 요청에 CSRF 토큰 자동 추가
apiClient.interceptors.request.use(config => {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
        config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    return config;
}, error => Promise.reject(error));

// 응답 인터셉터: 에러 코드별 처리
apiClient.interceptors.response.use(response => {
    return response; // 정상 응답은 그대로 반환
}, error => {
    if (error.response) {
        // HTTP 상태 코드에 따른 에러 메시지 처리
        switch (error.response.status) {
            case 400:
                alert('잘못된 요청입니다. 입력 값을 확인해주세요.');
                break;
            case 401:
                alert('인증되지 않은 요청입니다. 다시 로그인해주세요.');
                break;
            case 403:
                alert('권한이 없습니다.');
                break;
            case 404:
                alert('요청한 리소스를 찾을 수 없습니다.');
                break;
            case 500:
                alert('서버 오류가 발생했습니다. 다시 시도해주세요.');
                break;
            default:
                alert('알 수 없는 오류가 발생했습니다.');
        }
    } else {
        // 네트워크 오류 또는 서버가 응답하지 않는 경우
        alert('서버에 연결할 수 없습니다. 네트워크 상태를 확인해주세요.');
    }

    return Promise.reject(error);
});

export default apiClient;
