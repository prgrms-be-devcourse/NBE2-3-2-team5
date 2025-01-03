// 상수 정의
const API_ENDPOINTS = {
    USERS: '/api/admin/users',
    POSTS: '/api/admin/posts',
    COMPANIONS: '/api/companions'
};

const PAGINATION = {
    DEFAULT_PAGE: 1,
    PAGE_SIZE: 10
};


//헤더
fetch('../html/header.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('header-container').innerHTML = data;
    });



// 상태 관리
const state = {
    members: {
        currentPage: PAGINATION.DEFAULT_PAGE,
        editingUserId: null
    },
    posts: {
        currentPage: PAGINATION.DEFAULT_PAGE
    }
};

// 유틸리티 함수
const utils = {
    async fetchWithErrorHandling(url, options = {}) {
        try {
            const response = await fetch(url, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });

            if (!response.ok) {
                if (response.status === 204) {  // No Content
                    return null;
                }
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || `요청 실패 (${response.status})`);
            }

            // 응답이 있는 경우만 JSON 파싱
            return response.status !== 204 ? await response.json() : null;
        } catch (error) {
            console.error('API 요청 실패:', error);
            throw error;
        }
    },

    createTableRow(data, columns, actions) {
        const row = document.createElement('tr');
        row.setAttribute('data-id', data.userId);

        const cells = columns.map(column =>
            `<td class="${column.class || ''}">${column.render(data)}</td>`
        );

        /*
        const actionButtons = actions.map(action => {
            const button = document.createElement('button');
            button.textContent = action.text;
            button.addEventListener('click', () => action.handler(data.userId));
            return button.outerHTML;
        }).join('');

         */

        const actionButtons = actions.map(action => {
            return `<button onclick="memberManager.${action.text === '수정' ? 'editMember' : 'deleteMember'}(${data.userId})">${action.text}</button>`;
        }).join('');

        row.innerHTML = [...cells, `<td>${actionButtons}</td>`].join('');
        return row;
    }
};

// 회원 관리 모듈
const memberManager = {
    async loadMembers() {
        try {
            const data = await utils.fetchWithErrorHandling(
                `${API_ENDPOINTS.USERS}?page=${state.members.currentPage - 1}&size=${PAGINATION.PAGE_SIZE}`);
            console.log('API Response:', data);

            const memberTable = document.getElementById('member-table');
            memberTable.innerHTML = '';

            const columns = [
                { class: 'no', render: (member, index) => (state.members.currentPage - 1) * PAGINATION.PAGE_SIZE + index + 1 },
                { class: 'user-id', render: member => {
                        console.log('회원 ID 렌더링:', member);
                        return member.userId;
                    }},
                { class: 'user-name', render: member => member.userName },
                { class: 'user-nickname', render: member => member.nickname },
                { class: 'user-email', render: member => member.email },
                { class: 'user-role', render: member => member.role },
                { class: 'user-created-date', render: member => {
                        const date = new Date(member.createdDate);
                        return date.toLocaleDateString('ko-KR', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                    }},
                { class: 'user-gender', render: member => member.gender === 'M' ? '남성' : '여성' }
            ];

            const actions = [
                {
                    text: '수정',
                    handler: function(userId) {
                        memberManager.editMember(userId);
                    }
                },
                {
                    text: '삭제',
                    handler: function(userId) {
                        memberManager.deleteMember(userId);
                    }
                }
            ];

            data.content.forEach((member, index) => {
                //console.log('Member data:', JSON.stringify(member, null, 2));
                memberTable.appendChild(utils.createTableRow(member, columns.map(col => ({
                    ...col,
                    render: (data) => col.render(data, index)
                })), actions));
            });

            this.updatePagination(data);
        } catch (error) {
            alert(`회원 목록 로드 실패: ${error.message}`);
        }
    },

    async updatePagination(data) {
        const totalPages = data.totalPages;
        const currentPage = state.members.currentPage;

        document.getElementById('member-current-page').textContent = currentPage;
        document.getElementById('member-prev-page').disabled = currentPage === 1;
        document.getElementById('member-next-page').disabled = currentPage >= totalPages;
    },

    async editMember(userId) {
        const row = document.querySelector(`tr[data-id="${userId}"]`);
        if (!row) return;

        state.members.editingUserId = userId;
        const editSection = document.getElementById('edit-section');

        // 폼 필드 업데이트
        ['name', 'nickname', 'email', 'role'].forEach(field => {
            const value = row.querySelector(`.user-${field}`).textContent;
            document.getElementById(`edit-${field}`).value = value;
        });

        document.getElementById('edit-gender').value =
            row.querySelector('.user-gender').textContent === '남성' ? 'M' : 'F';

        editSection.style.display = 'block';
    },

    async updateMember(event) {
        event.preventDefault();

        const updatedUser = {
            userName: document.getElementById('edit-name').value,
            nickname: document.getElementById('edit-nickname').value,
            email: document.getElementById('edit-email').value,
            gender: document.getElementById('edit-gender').value,
            //role: document.getElementById('edit-role').value,
            ratingAvg: 5
        };

        try {
            await utils.fetchWithErrorHandling(
                `${API_ENDPOINTS.USERS}/${state.members.editingUserId}`,
                {
                    method: 'PUT',
                    body: JSON.stringify(updatedUser)
                }
            );

            alert('회원 정보가 수정되었습니다.');
            this.cancelEdit();
            this.loadMembers();
        } catch (error) {
            alert(`수정 실패: ${error.message}`);
        }
    },

    async deleteMember(userId) {
        if (!confirm('정말로 삭제하시겠습니까?')) return;

        try {
            await utils.fetchWithErrorHandling(
                `${API_ENDPOINTS.USERS}/${userId}`,
                { method: 'DELETE' }
            );

            alert('회원이 삭제되었습니다.');
            this.loadMembers();
        } catch (error) {
            alert(`삭제 실패: ${error.message}`);
        }
    },

    cancelEdit() {
        document.getElementById('edit-section').style.display = 'none';
        state.members.editingUserId = null;
    },

    handlePagination(direction) {
        if (direction === 'prev' && state.members.currentPage > 1) {
            state.members.currentPage--;
        } else if (direction === 'next') {
            state.members.currentPage++;
        }
        this.loadMembers();
    }
};

// 게시글 관리 모듈
const postManager = {
    async loadPosts() {
        try {
            const data = await utils.fetchWithErrorHandling(
                `${API_ENDPOINTS.COMPANIONS}?page=${state.posts.currentPage - 1}&size=${PAGINATION.PAGE_SIZE}`
            );

            const postTable = document.getElementById('post-table');
            postTable.innerHTML = '';

            const columns = [
                { render: post => post.id },
                { render: post => post.title },
                { render: post => post.writer },
                { render: post => post.views }
            ];

            const actions = [
                { text: '삭제', handler: 'postManager.deletePost' }
            ];

            data.content.forEach(post => {
                postTable.appendChild(utils.createTableRow(post, columns, actions));
            });

            this.updatePagination(data);
        } catch (error) {
            alert(`게시글 목록 로드 실패: ${error.message}`);
        }
    },

    async updatePagination(data) {
        document.getElementById('current-page').textContent = state.posts.currentPage;
        document.getElementById('prev-page').disabled = state.posts.currentPage === 1;
        document.getElementById('next-page').disabled = data.content.length < PAGINATION.PAGE_SIZE;
    },

    async deletePost(postId) {
        if (!confirm('정말로 삭제하시겠습니까?')) return;

        try {
            await utils.fetchWithErrorHandling(
                `${API_ENDPOINTS.POSTS}/${postId}`,
                { method: 'DELETE' }
            );

            alert('게시글이 삭제되었습니다.');
            this.loadPosts();
        } catch (error) {
            alert(`삭제 실패: ${error.message}`);
        }
    },

    handlePagination(direction) {
        if (direction === 'prev' && state.posts.currentPage > 1) {
            state.posts.currentPage--;
        } else if (direction === 'next') {
            state.posts.currentPage++;
        }
        this.loadPosts();
    }
};

// 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', () => {
    // 탭 전환 이벤트 설정
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            tab.classList.add('active');
            const activeTabContent = document.getElementById(`${tab.id}-management`);
            activeTabContent.classList.add('active');

            if (tab.id === 'member') {
                memberManager.loadMembers();
            } else if (tab.id === 'post') {
                postManager.loadPosts();
            }
        });
    });

    // 회원 관리 페이지네이션 이벤트
    document.getElementById('member-prev-page').addEventListener('click', () =>
        memberManager.handlePagination('prev'));
    document.getElementById('member-next-page').addEventListener('click', () =>
        memberManager.handlePagination('next'));

    // 게시글 관리 페이지네이션 이벤트
    document.getElementById('prev-page').addEventListener('click', () =>
        postManager.handlePagination('prev'));
    document.getElementById('next-page').addEventListener('click', () =>
        postManager.handlePagination('next'));

    // 회원 수정 폼 이벤트
    document.getElementById('edit-form').addEventListener('submit', (event) =>
        memberManager.updateMember(event));
    document.querySelector('#edit-section button[type="button"]').addEventListener('click', () =>
        memberManager.cancelEdit());

    // 초기 데이터 로드
    memberManager.loadMembers();
});