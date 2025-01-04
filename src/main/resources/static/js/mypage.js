import { apiRequest } from './apiClient.js'; // 공통 모듈 불러오기

// 사용자 데이터를 가져와서 HTML 업데이트
async function fetchUserData() {
    try {
        const data = await apiRequest('/api/user/mypage', {
            method: 'GET',
        });

        // 사용자 정보 업데이트
        document.getElementById('welcome-message').textContent = `Welcome, ${data.userName}`;
        document.getElementById('nickname').textContent = data.nickname;
        document.getElementById('email').textContent = data.email;
    } catch (error) {
        console.error('Error fetching user data:', error);
        alert('사용자 정보를 불러오지 못했습니다. 다시 시도해주세요.');
    }
}

// 탭 전환
function showTab(tabId) {
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => tab.classList.remove('active'));

    const contents = document.querySelectorAll('.tab-content');
    contents.forEach(content => content.classList.remove('active'));

    document.getElementById(tabId).classList.add('active');
    event.target.classList.add('active');
}

// 프로필 수정 페이지로 이동
function openEditPage() {
    window.location.href = "edit-profile.html";
}

// 이벤트 리스너 등록
function initializeEventListeners() {
    // 프로필 수정 버튼 클릭 이벤트
    document.getElementById('edit-profile-btn').addEventListener('click', openEditPage);

    // 탭 버튼 클릭 이벤트
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', (event) => {
            const tabId = event.target.getAttribute('data-tab');
            showTab(tabId);
        });
    });
}

// DOMContentLoaded 이벤트에서 초기화 실행
document.addEventListener('DOMContentLoaded', () => {
    fetchUserData(); // 사용자 데이터 가져오기
    initializeEventListeners(); // 이벤트 리스너 등록
});

