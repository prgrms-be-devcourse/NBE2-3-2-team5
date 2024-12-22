window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('festival_id');

    if (eventId) {
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    const event = JSON.parse(xhr.responseText.trim());
                    displayEventDetails(event);
                } else {
                    alert(`[에러] 축제 상세 정보 요청 실패: ${xhr.status}`);
                }
            }
        }
        xhr.open("GET", `/api/events/${eventId}`, true);
        xhr.send();
    } else {
        alert("축제가 존재하지 않습니다.");
    }
}

function displayEventDetails(event) {
    document.title = `${event.title} - Festimo`;

    document.querySelector('.event-title').innerHTML = `${event.title} <span class="event-category">${event.category}</span>`;

    // 진행상태 뱃지
    const badge = document.querySelector('.event-badge');
    const today = new Date();

    const startDate = new Date(event.startDate);
    const endDate = new Date(event.endDate);

    let badgeText = '';
    if (today >= startDate && today <= endDate) {
        badgeText = '진행중';
    } else if (today < startDate) {
        badgeText = '예정';
    } else if (today > endDate) {
        badgeText = '종료';
    }

    badge.textContent = badgeText;


    const imageElement = document.querySelector('.event-image img');
    imageElement.src = event.image;
    imageElement.alt = event.title;

    document.querySelectorAll('.info-row .info-value')[0].innerHTML = `${event.startDate} ~ ${event.endDate}`;

    document.querySelectorAll('.info-row .info-value')[1].innerHTML = event.address;

    document.querySelectorAll('.info-row .info-value')[2].innerHTML = event.phone;

    const descriptions = document.querySelectorAll('.event-description');
    descriptions[0].textContent = event.mainDescription; // 메인 설명
    descriptions[1].innerHTML = event.programDetails.replace(/\n/g, '<br>'); // 프로그램 세부내용 HTML 변환

    // // 위치 섹션의 지도 (예: 좌표를 사용하여 지도 생성)
    // const mapButton = document.querySelector('.map-button');
    // mapButton.onclick = () => {
    //     window.open(`https://maps.google.com/?q=${event.latitude},${event.longitude}`, '_blank');
    // };
}
/*
fetch('/api/map-key')
    .then(response => response.text())
    .then(apiKey => {
        const script = document.createElement('script');
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}`;
        document.head.appendChild(script);
    })
    .catch(error => console.error('Error fetching API key:', error));

var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};

var map = new kakao.maps.Map(container, options);
 */