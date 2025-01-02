// API 호출 시 공통으로 사용할 headers

/*
//테스트용
function getHeaders() {
    //const token = localStorage.getItem('token');

    //const token = "BearereyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzM1ODMzMDU1LCJleHAiOjE3MzU4MzY2NTV9.Kwf3f7hbeTrzUfuLOOj2QNTFWJvpaHpMIC_6BGri5X8TYvPI48VBoYQWxmx4y_sj";

    return {
        'Authorization': 'Bearer ' + "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzM1ODQwNTA2LCJleHAiOjE3MzU4NDQxMDZ9.yOjj1mEiiR4FpbCIiXY_ey7zVm_JPKykMy4iky9wuCUEZDrbrVnjtH_ysF9e7Y8M"
        ,
        'Content-Type': 'application/json'
    };
}
 */

function getHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
    };
}



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

// 동행 데이터 가져오기
async function fetchCompanions() {
    try {
        document.getElementById('leaderContent').innerHTML = '<div class="loading">로딩 중...</div>';
        document.getElementById('memberContent').innerHTML = '<div class="loading">로딩 중...</div>';

        const response = await fetch('/api/meet/companions/mine', {
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        const data = await response.json();
        renderCompanions(data);
        return data;
    } catch (error) {
        console.error('Error:', error);
        const errorMessage = '<div class="error">데이터를 불러오는데 실패했습니다.</div>';
        document.getElementById('leaderContent').innerHTML = errorMessage;
        document.getElementById('memberContent').innerHTML = errorMessage;
        return { asLeader: [], asMember: [] };
    }
}


function renderCompanions(data) {
    // 리더로 참여한 동행 렌더링
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
            `).join('')}
        </div>
    `).join('') || '<div style="text-align: center; padding: 20px;">리더로 참여한 동행이 없습니다.</div>';

    // 멤버로 참여한 동행 렌더링
    const memberContent = document.getElementById('memberContent');
    memberContent.innerHTML = data.asMember.map(companion => `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn" onclick="handleWithdraw(${companion.companionId})">탈퇴</button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
            `).join('')}
        </div>
    `).join('') || '<div style="text-align: center; padding: 20px;">동행원으로 참여한 동행이 없습니다.</div>';
}

// 신청 리스트 로드
async function loadApplications(companionId) {

    // 모달 열기 추가
    document.getElementById('applicationModal').style.display = 'block';


    try {
        const response = await fetch(`/api/meet/companion/${companionId}`, {
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        const data = await response.json();
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

// 수락 함수
async function acceptApplication(applicationId, companionId) {
    try {
        const response = await fetch(`/api/meet/${applicationId}/accept`, {
            method: "POST",
            headers: getHeaders()
        });

        if (response.ok) {
            alert("신청을 수락했습니다.");
            loadApplications(companionId);
            fetchCompanions();

        } else {
            alert("신청 수락에 실패했습니다.");
        }
    } catch (error) {
        console.error("Error accepting application:", error);
        alert("신청 수락 중 문제가 발생했습니다.");
    }
}

// 거절 함수
async function rejectApplication(applicationId, companionId) {
    try {
        const response = await fetch(`/api/meet/${applicationId}/reject`, {
            method: "PATCH",
            headers: getHeaders()
        });

        if (response.ok) {
            alert("신청을 거절했습니다.");
            loadApplications(companionId);
            fetchCompanions();

        } else {
            alert("신청 거절에 실패했습니다.");
        }
    } catch (error) {
        console.error("Error rejecting application:", error);
        alert("신청 거절 중 문제가 발생했습니다.");
    }
}

// 탈퇴 함수
async function handleWithdraw(companionId) {
    if (confirm('정말로 이 동행에서 탈퇴하시겠습니까?')) {
        try {
            const response = await fetch(`/api/meet/${companionId}`, {
                method: "DELETE",
                headers: getHeaders()
            });

            if (response.ok) {
                alert("동행에서 성공적으로 탈퇴했습니다.");
                initializeContent();
            } else {
                alert("탈퇴에 실패했습니다. 다시 시도해주세요.");
            }
        } catch (error) {
            console.error("동행 취소 중 오류 발생:", error);
            alert("탈퇴 처리 중 문제가 발생했습니다.");
        }
    }
}
