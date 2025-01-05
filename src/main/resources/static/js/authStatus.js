import { apiRequest } from './apiClient.js';

// 로그인 상태 확인 및 헤더 업데이트
export async function updateAuthLink() {
    try {
        // apiRequest를 통해 Authorization 헤더 포함
        const isLoggedIn = await apiRequest('/api/status', { method: 'GET' });
        const authLink = document.querySelector('.auth-link');
        console.log('updateAuthLink loaded:', typeof updateAuthLink);
        if (!authLink) {
            console.error('Auth link element not found.');
            return;
        }

        if (isLoggedIn) {
            // 로그인 상태: 'Logout'으로 변경
            authLink.textContent = 'Logout';
            authLink.setAttribute('href', '#'); // 로그아웃 동작 설정
            authLink.addEventListener('click', async (event) => {
                event.preventDefault();
                await handleLogout();
            });
        } else {
            // 비로그인 상태: 'Login'으로 유지
            authLink.textContent = 'Login';
            authLink.setAttribute('href', '/html/login.html');
        }
    } catch (error) {
        console.error('Error updating auth link:', error);
    }
}

// 로그아웃 처리 함수
async function handleLogout() {
    try {
        await fetch('/api/logout', { method: 'POST', credentials: 'include' });
        // 로그아웃 성공 후 localStorage에서 accessToken 삭제
        localStorage.removeItem('accessToken');
        alert('로그아웃되었습니다.');

        // 헤더 링크를 초기 상태로 변경
        const authLink = document.querySelector('.auth-link');
        authLink.textContent = 'Login';
        authLink.setAttribute('href', '/html/login.html');

        window.location.href = '/'; // 로그아웃 후 메인 페이지로 이동
    } catch (error) {
        console.error('Error during logout:', error);
        alert('로그아웃 중 문제가 발생했습니다.');
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', updateAuthLink);
