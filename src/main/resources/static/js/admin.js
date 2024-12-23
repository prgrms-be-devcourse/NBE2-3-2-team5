let editingUserId = null; // 현재 수정 중인 회원 ID

document.addEventListener("DOMContentLoaded", function () {
    const tabs = document.querySelectorAll(".tab");
    const tabContents = document.querySelectorAll(".tab-content");

    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active"));
            tabContents.forEach(content => content.classList.remove("active"));

            tab.classList.add("active");
            const activeTabContent = document.getElementById(`${tab.id}-management`);
            activeTabContent.classList.add("active");

            if (tab.id === "member") loadMembers();
            if (tab.id === "post") loadPosts();
            if (tab.id === "review") loadReviews();
        });
    });

    loadMembers(); // 페이지 로드 시 회원 목록 불러오기
});

function loadMembers() {
    fetch("/api/admin/users?page=1&size=10") // REST API 호출
        .then(response => response.json())
        .then(data => {
            const memberTable = document.getElementById("member-table");
            memberTable.innerHTML = "";

            data.content.forEach(member => {
                const row = document.createElement("tr");
                row.setAttribute("data-user-id", member.userId);

                row.innerHTML = `
                    <td class="user-id">${member.userId}</td>
                    <td class="user-name">${member.userName}</td>
                    <td class="user-nickname">${member.nickname}</td>
                    <td class="user-email">${member.email}</td>
                    <td class="user-role">${member.role}</td>
                    <td class="user-created-date">${member.createdDate}</td>
                    <td class="user-gender">${member.gender === "M" ? "남성" : "여성"}</td>
                    <td>
                        <button onclick="editMember(${member.userId})">수정</button>
                        <button onclick="deleteMember(${member.userId})">삭제</button>
                    </td>
                `;
                memberTable.appendChild(row);
            });
        })
        .catch(error => console.error("Error loading members:", error));
}

function editMember(userId) {
    // 테이블에서 해당 userId를 가진 행을 찾음
    const row = document.querySelector(`tr[data-user-id="${userId}"]`);

    if (row) {
        // 행에서 각 데이터를 가져와서 수정 폼에 채우기
        const userName = row.querySelector('.user-name').textContent;
        const nickname = row.querySelector('.user-nickname').textContent;
        const email = row.querySelector('.user-email').textContent;
        const role = row.querySelector('.user-role').textContent;
        const gender = row.querySelector('.user-gender').textContent === '남성' ? 'M' : 'F';

        // 수정 폼에 데이터 채우기
        document.getElementById("edit-name").value = userName;
        document.getElementById("edit-nickname").value = nickname;
        document.getElementById("edit-email").value = email;
        document.getElementById("edit-role").value = role;
        document.getElementById("edit-gender").value = gender;

        // 수정 섹션 표시
        document.getElementById("edit-section").style.display = "block";
        editingUserId = userId; // 수정 중인 사용자 ID 저장
    }
}

document.getElementById("edit-form").addEventListener("submit", function (event) {
    event.preventDefault();

    const updatedUser = {
        userName: document.getElementById("edit-name").value,
        nickname: document.getElementById("edit-nickname").value,
        email: document.getElementById("edit-email").value,
        gender: document.getElementById("edit-gender").value,
        ratingAvg: 5
    };

    fetch(`/api/admin/users/${editingUserId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedUser),
    })
        .then(response => {
            if (response.ok) {
                // 테이블 데이터 업데이트
                const row = document.querySelector(`tr[data-user-id="${editingUserId}"]`);
                row.querySelector('.user-name').textContent = updatedUser.userName;
                row.querySelector('.user-nickname').textContent = updatedUser.nickname;
                row.querySelector('.user-email').textContent = updatedUser.email;
                row.querySelector('.user-gender').textContent = updatedUser.gender === "M" ? "남성" : "여성";

                alert("회원 정보가 수정되었습니다.");
                cancelEdit();
            } else {
                throw new Error('수정 실패');
            }
        })
        .catch(error => {
            alert("수정에 실패했습니다.");
        });
});

function cancelEdit() {
    document.getElementById("edit-section").style.display = "none";
    editingUserId = null;
}

function deleteMember(userId) {
    if (!confirm("정말로 삭제하시겠습니까?")) return;

    fetch(`/api/admin/${userId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then(response => {
            if (response.ok) {
                const row = document.querySelector(`tr[data-user-id="${userId}"]`);
                if (row) row.remove();
                alert("회원이 삭제되었습니다.");
            } else {
                throw new Error(`Failed to delete user: ${response.statusText}`);
            }
        })
        .catch(error => {
            console.error("Error deleting user:", error);
            alert("회원 삭제에 실패했습니다.");
        });
}

function loadPosts() {
    fetch("/api/companions?page=1&size=10")
        .then(response => response.json())
        .then(data => {
            const postTable = document.getElementById("post-table");
            postTable.innerHTML = "";

            data.content.forEach(post => {
                const row = document.createElement("tr");
                row.setAttribute("data-post-id", post.id);

                row.innerHTML = `
                    <td>${post.id}</td>
                    <td>${post.title}</td>
                    <td>${post.writer}</td>
                    <td>${post.views}</td>
                    <td>
                        <button onclick="deletePost(${post.id})">삭제</button>
                    </td>
                `;
                postTable.appendChild(row);
            });
        })
        .catch(error => console.error("Error loading posts:", error));
}

function deletePost(postId) {
    if (!confirm("정말로 삭제하시겠습니까?")) return;

    fetch(`/api/admin/posts/${postId}`, {
        method: "DELETE",
    })
        .then(response => {
            if (response.ok) {
                alert("게시글이 삭제되었습니다.");
                loadPosts(); // 게시글 목록 다시 로드
            } else {
                alert("삭제에 실패했습니다.");
            }
        })
        .catch(error => console.error("Error deleting post:", error));
}

function loadReviews() {
    console.log("리뷰 관리 기능 준비 중...");
}
