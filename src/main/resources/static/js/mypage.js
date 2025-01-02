document.addEventListener('DOMContentLoaded', () => {
    const nicknameElement = document.getElementById('nickname');
    const emailElement = document.getElementById('email');

    // 유저 정보 불러오기
    const fetchUserInfo = async () => {
        const token = localStorage.getItem("accessToken");

        if (!token) {
            alert("로그인이 필요합니다.");
            return;
        }

        try {
            const response = await fetch("/api/user/mypage", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const data = await response.json();
                nicknameElement.textContent = `${data.nickname}`;
                emailElement.textContent = `${data.email}`;
            } else {
                console.error("Error fetching user info:", response.status);
            }
        } catch (error) {
            console.error("Error:", error);
        }
    };

    // 페이지 로드 시 유저 정보 불러오기
    fetchUserInfo();

    // 버튼 이벤트 추가
    document.getElementById("editProfileBtn").addEventListener("click", () => {
        window.location.href = "/html/edit_profile.html";
    });

    document.getElementById("changePasswordBtn").addEventListener("click", () => {
        window.location.href = "/html/change_password.html";
    });

    document.getElementById("manageReviewsBtn").addEventListener("click", () => {
        window.location.href = "/html/manage_reviews.html";
    });
});
