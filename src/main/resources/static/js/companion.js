const userId = 8; // 로그인 기능 구현 전 임시로 설정된 사용자 ID



// API로부터 데이터를 가져오는 함수
async function fetchCompanions() {
    try {
        // 로딩 표시
        document.getElementById('leaderContent').innerHTML = '<div class="loading">로딩 중...</div>';
        document.getElementById('memberContent').innerHTML = '<div class="loading">로딩 중...</div>';

        // API 호출
        const response = await fetch(`http://localhost:8080/api/meet/companions/mine/${userId}`);

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('데이터 가져오기 오류:', error);
        const errorMessage = '<div class="error">데이터를 불러오는데 실패했습니다. 나중에 다시 시도해주세요.</div>';
        document.getElementById('leaderContent').innerHTML = errorMessage;
        document.getElementById('memberContent').innerHTML = errorMessage;
        return { asLeader: [], asMember: [] };
    }
}


// 리더로 참여한 동행 카드 생성
function renderLeaderCompanion(companion) {
    return `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn" onclick="handleCheckApplications(${companion.companionId})">
                    신청 리스트 확인
                </button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
            `).join('')}
        </div>
    `;
}

// 동행원으로 참여한 동행 카드 생성
function renderMemberCompanion(companion) {
    return `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn" onclick="handleWithdraw(${companion.companionId}, ${userId})">
                    탈퇴
                </button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
            `).join('')}
        </div>
    `;
}



// 모달 관련 함수들
function openModal() {
    document.getElementById('applicationModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('applicationModal').style.display = 'none';
}

// 신청 리스트 확인 버튼 클릭 핸들러
function handleCheckApplications(companionId) {
    openModal();
    loadApplications(companionId);
}

// 신청 리스트 로드
function loadApplications(companionId) {
    fetch(`/api/meet/companion/${companionId}`) // company -> companion으로 수정
        .then(response => response.json())
        .then(data => {
            console.log('받아온 데이터:', data);
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
                        사용자 ID: ${application.userId}<br>
                        신청일: ${new Date(application.appliedDate).toLocaleDateString()}
                    </div>
                    <div class="application-actions">
                        <button onclick="acceptApplication(${application.applicationId}, ${companionId})" class="accept-button">수락</button>
                        <button onclick="rejectApplication(${application.applicationId}, ${companionId})" class="reject-button">거절</button>
                    </div>
                `;
                applicationTable.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error loading applications:", error);
            document.getElementById("application-table").innerHTML =
                '<div class="error">데이터를 불러오는데 실패했습니다.</div>';
        });
}

// 수락 함수
function acceptApplication(applicationId, companionId) {
    fetch(`/api/meet/${applicationId}/accept`, {
        method: "POST"
    })
        .then(response => {
            if (response.ok) {
                alert("신청을 수락했습니다.");
                loadApplications(companionId);
            } else {
                alert("신청 수락에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("Error accepting application:", error);
            alert("신청 수락 중 문제가 발생했습니다.");
        });
}

// 거절 함수
function rejectApplication(applicationId, companionId) {
    fetch(`/api/meet/${applicationId}/reject`, {
        method: "PATCH"
    })
        .then(response => {
            if (response.ok) {
                alert("신청을 거절했습니다.");
                loadApplications(companionId);
            } else {
                alert("신청 거절에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("Error rejecting application:", error);
            alert("신청 거절 중 문제가 발생했습니다.");
        });
}

// 탈퇴 버튼 클릭 핸들러
function handleWithdraw(companionId) {
    if (confirm('정말로 이 동행에서 탈퇴하시겠습니까?')) {
        fetch(`/api/meet/${companionId}/users/${userId}`, {
            method: "DELETE"
        })
            .then(response => {
                if (response.ok) {
                    alert("동행에서 성공적으로 탈퇴했습니다.");
                    initializeContent(); // 탈퇴 후 동행 리스트 갱신
                } else {
                    alert("탈퇴에 실패했습니다. 다시 시도해주세요.");
                }
            })
            .catch(error => {
                console.error("동행 취소 중 오류 발생:", error);
                alert("탈퇴 처리 중 문제가 발생했습니다.");
            });
    }
}





// 탭 전환 로직
document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

        tab.classList.add('active');
        document.getElementById(`${tab.dataset.tab}Tab`).classList.add('active');
    });
});

// 모달 닫기 이벤트
document.querySelector('.close-btn').addEventListener('click', closeModal);
window.addEventListener('click', (event) => {
    const modal = document.getElementById('applicationModal');
    if (event.target === modal) {
        closeModal();
    }
});

// 초기 데이터 로딩 및 렌더링
async function initializeContent() {
    const data = await fetchCompanions();

    const leaderContent = document.getElementById('leaderContent');
    const memberContent = document.getElementById('memberContent');

    // 리더로 참여한 동행 렌더링
    leaderContent.innerHTML = data.asLeader.length ?
        data.asLeader.map(renderLeaderCompanion).join('') :
        '<div style="text-align: center; padding: 20px;">리더로 참여한 동행이 없습니다.</div>';

    // 동행원으로 참여한 동행 렌더링
    memberContent.innerHTML = data.asMember.length ?
        data.asMember.map(renderMemberCompanion).join('') :
        '<div style="text-align: center; padding: 20px;">동행원으로 참여한 동행이 없습니다.</div>';
}

// 페이지 로드시 실행
document.addEventListener('DOMContentLoaded', initializeContent);