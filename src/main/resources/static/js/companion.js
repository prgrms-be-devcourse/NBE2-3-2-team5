import {apiRequest} from './apiClient.js';

// 전역 함수로 등록
window.loadApplications = loadApplications;
window.handleWithdraw = handleWithdraw;
window.acceptApplication = acceptApplication;
window.rejectApplication = rejectApplication;

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

            tab.classList.add('active');
            const tabId = tab.dataset.tab;
            document.getElementById(`${tabId}Tab`).classList.add('active');
        });
    });
    fetchCompanions();
});

async function fetchCompanions() {
    try {
        document.getElementById('leaderContent').innerHTML = '<div class="loading">로딩 중...</div>';
        document.getElementById('memberContent').innerHTML = '<div class="loading">로딩 중...</div>';

        const data = await apiRequest('/api/meet/companions/mine');
        renderCompanions(data);
        console.log(data);
        return data;
    } catch (error) {
        console.error('Error:', error);
        const errorMessage = '<div class="error">데이터를 불러오는데 실패했습니다.</div>';
        document.getElementById('leaderContent').innerHTML = errorMessage;
        document.getElementById('memberContent').innerHTML = errorMessage;
        return {asLeader: [], asMember: []};
    }
}

function renderCompanions(data) {
    // 리더로 참여한 동행 렌더링
    console.log('Rendering leader data:', data.asLeader);
    const leaderContent = document.getElementById('leaderContent');
    leaderContent.innerHTML = data.asLeader.map(companion => `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn" onclick="loadApplications(${companion.companionId})">신청 리스트 확인</button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
                <button class="btn write-review-btn" 
                    data-id="${member.userId}" 
                    data-name="${member.userName}" 
                    data-companion-id="${companion.companionId}">
                    리뷰 작성
                </button>
            `).join('')}
        </div>
    `).join('') || '<div style="text-align: center; padding: 20px;">리더로 참여한 동행이 없습니다.</div>';

    // 멤버로 참여한 동행 렌더링
    console.log('Rendering Mem data:', data.asMember);

    const memberContent = document.getElementById('memberContent');
    memberContent.innerHTML = data.asMember.map(companion => `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn" onclick="handleWithdraw(${companion.companionId})">탈퇴</button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
        </div>
    `).join('') || '<div style="text-align: center; padding: 20px;">동행원으로 참여한 동행이 없습니다.</div>';
}

async function loadApplications(companionId) {
    document.getElementById('applicationModal').style.display = 'block';

    try {
        const data = await apiRequest(`/api/meet/companion/${companionId}`);
        const applicationTable = document.getElementById("application-table");
        applicationTable.innerHTML = "";

        if (!data || !Array.isArray(data) || data.length === 0) {
            applicationTable.innerHTML = '<div class="error">신청된 내역이 없습니다.</div>';
            return;
        }

        data.forEach(application => {
            const row = document.createElement("div");
            row.classList.add("application-row");
            row.innerHTML = `
                <div class="application-info">
                    닉네임: ${application.nickname}<br>
                    신청일: ${new Date(application.appliedDate).toLocaleDateString()}
                </div>
                <div class="application-actions">
                    <button onclick="acceptApplication(${application.applicationId}, ${companionId})" class="accept-button">수락</button>
                    <button onclick="rejectApplication(${application.applicationId}, ${companionId})" class="reject-button">거절</button>
                </div>
            `;
            applicationTable.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading applications:", error);
        document.getElementById("application-table").innerHTML =
            '<div class="error">데이터를 불러오는데 실패했습니다.</div>';
    }
}

// 모달 닫기 버튼 이벤트
document.querySelector('.close-btn').addEventListener('click', () => {
    document.getElementById('applicationModal').style.display = 'none';
});

async function acceptApplication(applicationId, companionId) {
    try {
        await apiRequest(`/api/meet/${applicationId}/accept`, {method: "POST"});
        alert("신청을 수락했습니다.");
        loadApplications(companionId);
        fetchCompanions();
    } catch (error) {
        console.error("Error accepting application:", error);
        alert("신청 수락 중 문제가 발생했습니다.");
    }
}

async function rejectApplication(applicationId, companionId) {
    try {
        await apiRequest(`/api/meet/${applicationId}/reject`, {method: "PATCH"});
        alert("신청을 거절했습니다.");
        loadApplications(companionId);
        fetchCompanions();
    } catch (error) {
        console.error("Error rejecting application:", error);
        alert("신청 거절 중 문제가 발생했습니다.");
    }
}

async function handleWithdraw(companionId) {
    if (confirm('정말로 이 동행에서 탈퇴하시겠습니까?')) {
        try {
            await apiRequest(`/api/meet/${companionId}`, {method: "DELETE"});
            alert("동행에서 성공적으로 탈퇴했습니다.");
            fetchCompanions();
        } catch (error) {
            console.error("동행 취소 중 오류 발생:", error);
            alert("탈퇴 처리 중 문제가 발생했습니다.");
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // 리뷰 작성 버튼 클릭 이벤트 등록
    document.body.addEventListener('click', (event) => {
        if (event.target.classList.contains('write-review-btn')) {
            const userId = event.target.dataset.id;
            const userName = event.target.dataset.name;
            const companionId = event.target.dataset['companion-id']; // 추가된 companionId 데이터 속성

            openReviewModal(userId, userName, companionId); // companionId 전달
        }
    });

    // 모달 닫기 이벤트
    document.getElementById('reviewModal').querySelector('.close-btn').addEventListener('click', () => {
        const reviewModal = document.getElementById('reviewModal');
        reviewModal.style.display = 'none'; // 리뷰 작성 모달 닫기
    });


    // 리뷰 작성 폼 제출 이벤트
    document.getElementById('reviewForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const reviewContent = document.getElementById('reviewContent').value;
        const reviewRating = document.getElementById('reviewRating').value;
        const revieweeId = document.getElementById('reviewModal').dataset.revieweeId;
        const companionId = document.getElementById('reviewModal').dataset.companionId; // companionId 사용

        if (!reviewContent || !reviewRating) {
            alert('내용과 평점을 입력해주세요.');
            return;
        }

        try {
            await submitReview({
                revieweeId,
                content: reviewContent,
                rating: parseInt(reviewRating),
                companionId: companionId, // companionId 전달
            });
            alert('리뷰가 작성되었습니다.');
            closeReviewModal();
        } catch (error) {
            console.error('리뷰 작성 오류:', error);
            alert('리뷰 작성에 실패했습니다.');
        }
    });
});

function openReviewModal(revieweeId, userName, companionId) {
    console.log("Review Modal Opened with:", {revieweeId, userName, companionId}); // 디버깅용 로그 추가
    const reviewModal = document.getElementById('reviewModal');
    reviewModal.dataset.revieweeId = revieweeId;
    reviewModal.dataset.companionId = companionId;
    reviewModal.querySelector('h2').textContent = `${userName}님에게 리뷰 작성`;
    reviewModal.style.display = 'block';
}


function closeReviewModal() {
    const reviewModal = document.getElementById('reviewModal');
    reviewModal.style.display = 'none';
    document.getElementById('reviewContent').value = '';
    document.getElementById('reviewRating').value = '5';
}

async function submitReview(reviewData) {
    try {
        console.log('Submitting review:', reviewData); // revieweeId 확인
        const response = await apiRequest('/api/reviews', {
            method: 'POST',
            body: JSON.stringify({
                revieweeId: reviewData.revieweeId,
                content: reviewData.content,
                rating: reviewData.rating,
            }),
        });

        return response; // API 호출 결과 반환
    } catch (error) {
        console.error('리뷰 작성 오류:', error);
        throw new Error('리뷰 작성 실패');
    }
}
