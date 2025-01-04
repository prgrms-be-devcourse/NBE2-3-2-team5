const data = {
    "asLeader": [
        {
            "companionId": 10,
            "leaderId": 12,
            "leaderName": "user12",
            "members": [
                {"userId": 23, "userName": "user23"},
                {"userId": 24, "userName": "user24"},
                {"userId": 25, "userName": "user25"}
            ]
        }
    ],
    "asMember": [
        {
            "companionId": 1,
            "leaderId": 4,
            "leaderName": "string",
            "members": [
                {"userId": 12, "userName": "user12"},
                {"userId": 25, "userName": "user25"},
                {"userId": 30, "userName": "user30"},
                {"userId": 31, "userName": "user31"}
            ]
        }
    ]
};

function renderLeaderCompanion(companion) {
    return `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn">신청 리스트 확인</button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
            `).join('')}
        </div>
    `;
}

function renderMemberCompanion(companion) {
    return `
        <div class="companion-card">
            <div class="card-header">
                <div class="member-title">리더</div>
                <button class="btn">탈퇴</button>
            </div>
            <div class="member-box">${companion.leaderName}</div>
            ${companion.members.map((member, index) => `
                <div class="member-title">동행원${index + 1}</div>
                <div class="member-box">${member.userName}</div>
            `).join('')}
        </div>
    `;
}

// Tab switching logic
document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

        tab.classList.add('active');
        document.getElementById(`${tab.dataset.tab}Tab`).classList.add('active');
    });
});

// Render initial content
const listTab = document.getElementById('listTab');
const mineTab = document.getElementById('mineTab');

data.asLeader.forEach(companion => {
    listTab.innerHTML += renderLeaderCompanion(companion);
});

data.asMember.forEach(companion => {
    mineTab.innerHTML += renderMemberCompanion(companion);
});
