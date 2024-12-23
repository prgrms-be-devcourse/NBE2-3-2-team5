document.addEventListener("DOMContentLoaded", function () {
    loadApplications(2); // 2는 예제 companyId
});

/*
function loadApplications(companyId) {
    fetch(`/api/meet/company/${companyId}`)
        .then(response => response.json())
        .then(data => {
            const applicationTable = document.getElementById("application-table");
            applicationTable.innerHTML = ""; // 기존 데이터 초기화

            data.forEach(application => {
                const row = document.createElement("div");
                row.classList.add("application-row");
                row.setAttribute("data-application-id", application.applicationId);

                row.innerHTML = `
                    <div class="application-info">
                        사용자 ID: ${application.userId}
                    </div>
                    <div class="application-actions">
                        <button onclick="acceptApplication(${application.applicationId})" class="accept-button">수락</button>
                        <button onclick="rejectApplication(${application.applicationId})" class="reject-button">거절</button>
                    </div>
                `;
                applicationTable.appendChild(row);
            });
        })
        .catch(error => console.error("Error loading applications:", error));
}
*/
function loadApplications(companionId) {
    fetch(`/api/meet/company/${companionId}`)
        .then(response => response.json())
        .then(data => {
            const applicationTable = document.getElementById("application-table");
            applicationTable.innerHTML = ""; // 기존 데이터 초기화

            data.forEach(application => {
                const row = document.createElement("div");
                row.classList.add("application-row");
                row.setAttribute("data-application-id", application.applicationId);

                row.innerHTML = `
                    <div class="application-info">
                        사용자 ID: ${application.userId}
                    </div>
                    <div class="application-actions">
                        <button onclick="acceptApplication(${application.applicationId})" class="accept-button">수락</button>
                        <button onclick="rejectApplication(${application.applicationId})" class="reject-button">거절</button>
                    </div>
                `;
                applicationTable.appendChild(row);
            });
        })
        .catch(error => console.error("Error loading applications:", error));
}


function acceptApplication(applicationId) {
    fetch(`/api/meet/${applicationId}/accept`, {
        method: "POST", // HTTP 메서드: POST
    })
        .then(response => {
            if (response.ok) {
                alert("신청을 수락했습니다."); // 성공 메시지
                loadApplications(2); // 다시 신청 리스트 불러오기 (예제 companionId)
            } else {
                alert("신청 수락에 실패했습니다."); // 실패 메시지
            }
        })
        .catch(error => {
            console.error("Error accepting application:", error); // 에러 출력
            alert("신청 수락 중 문제가 발생했습니다.");
        });
}

function rejectApplication(applicationId) {
    fetch(`/api/meet/${applicationId}/reject`, {
        method: "PATCH", // HTTP 메서드: PATCH
    })
        .then(response => {
            if (response.ok) {
                alert("신청을 거절했습니다."); // 성공 메시지
                loadApplications(2); // 다시 신청 리스트 불러오기 (예제 companionId)
            } else {
                alert("신청 거절에 실패했습니다."); // 실패 메시지
            }
        })
        .catch(error => {
            console.error("Error rejecting application:", error); // 에러 출력
            alert("신청 거절 중 문제가 발생했습니다.");
        });
}

