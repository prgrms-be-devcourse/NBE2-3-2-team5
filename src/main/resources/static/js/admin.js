let editingUserId = null; // 현재 수정 중인 회원 ID
let memberCurrentPage = 1; // 회원 관리 현재 페이지
const memberPageSize = 10; // 회원 관리 페이지당 아이템 수

document.addEventListener("DOMContentLoaded", function () {
    const tabs = document.querySelectorAll(".tab");
    const tabContents = document.querySelectorAll(".tab-content");

    // 탭 전환 기능
    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active"));
            tabContents.forEach(content => content.classList.remove("active"));

            tab.classList.add("active");
            const activeTabContent = document.getElementById(`${tab.id}-management`);
            activeTabContent.classList.add("active");

            if (tab.id === "member") {
                loadMembers();
                updateMemberPagination();
            }
        });
    });

    // 초기 로드
    loadMembers();
    updateMemberPagination();
});

// 회원 관리 페이지네이션 버튼 동작
document.getElementById("member-prev-page").addEventListener("click", () => {
    if (memberCurrentPage > 1) {
        memberCurrentPage--;
        updateMemberPagination();
        loadMembers();
    }
});

document.getElementById("member-next-page").addEventListener("click", () => {
    memberCurrentPage++;
    updateMemberPagination();
    loadMembers();
});

function updateMemberPagination() {
    document.getElementById("member-current-page").textContent = memberCurrentPage;
    document.getElementById("member-prev-page").disabled = memberCurrentPage === 1;

    fetch(`/api/admin/users?page=${memberCurrentPage}&size=${memberPageSize}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById("member-next-page").disabled = data.content.length < memberPageSize;
        })
        .catch(error => console.error("Error updating member pagination:", error));
}

function loadMembers() {
    fetch(`/api/admin/users?page=${memberCurrentPage}&size=${memberPageSize}`)
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
    const row = document.querySelector(`tr[data-user-id="${userId}"]`);

    if (row) {
        const userName = row.querySelector('.user-name').textContent;
        const nickname = row.querySelector('.user-nickname').textContent;
        const email = row.querySelector('.user-email').textContent;
        const role = row.querySelector('.user-role').textContent;
        const gender = row.querySelector('.user-gender').textContent === '남성' ? 'M' : 'F';

        document.getElementById("edit-name").value = userName;
        document.getElementById("edit-nickname").value = nickname;
        document.getElementById("edit-email").value = email;
        document.getElementById("edit-role").value = role;
        document.getElementById("edit-gender").value = gender;

        document.getElementById("edit-section").style.display = "block";
        editingUserId = userId;
    }
}

document.getElementById("edit-form").addEventListener("submit", function (event) {
    event.preventDefault();

    const updatedUser = {
        userName: document.getElementById("edit-name").value,
        nickname: document.getElementById("edit-nickname").value,
        email: document.getElementById("edit-email").value,
        gender: document.getElementById("edit-gender").value,
        role: document.getElementById("edit-role").value,
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
            if (!response.ok) {
                throw new Error('수정 실패');
            }
            return response.json();
        })
        .then(data => {
            alert("회원 정보가 수정되었습니다.");
            cancelEdit();
            loadMembers();
        })
        .catch(error => {
            console.error("Error updating user:", error);
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
        .then(async response => {
            // 응답 상태 확인 및 에러 메시지 추출
            if (!response.ok) {
                // 서버에서 보낸 에러 메시지가 있다면 추출
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || `삭제 실패 (${response.status})`);
            }
            return response;
        })
        .then(() => {
            alert("회원이 삭제되었습니다.");
            loadMembers(); // 회원 목록 다시 로드
            updateMemberPagination(); // 페이지네이션 업데이트
        })
        .catch(error => {
            console.error("회원 삭제 중 오류 발생:", error);
            alert(`회원 삭제에 실패했습니다. ${error.message}`);
        });
}


/**
 *
 *
 *
 *
 * */




let currentPage = 1; // 현재 페이지
const pageSize = 10; // 페이지당 아이템 수

document.getElementById("prev-page").addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        updatePagination();
        loadPosts();
    }
});

document.getElementById("next-page").addEventListener("click", () => {
    currentPage++;
    updatePagination();
    loadPosts();
});

function updatePagination() {
    document.getElementById("current-page").textContent = currentPage;

    // 이전 페이지 버튼 활성화/비활성화
    document.getElementById("prev-page").disabled = currentPage === 1;

    // 다음 페이지 버튼은 조건에 따라 활성화 (예: 서버에서 총 페이지 수를 반환하면 활용 가능)
}

function loadPosts() {
    fetch(`/api/companions?page=${currentPage}&size=${pageSize}`)
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

            // 다음 페이지 버튼 활성화/비활성화 (예: 데이터가 비었을 때 비활성화)
            document.getElementById("next-page").disabled = data.content.length < pageSize;
        })
        .catch(error => console.error("Error loading posts:", error));
}

// 초기 페이지 로드
loadPosts();
updatePagination();

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
