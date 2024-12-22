window.onload = function() {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if ( xhr.readyState == 4 ) {
            if ( xhr.status == 200 ) {
                const events = JSON.parse(xhr.responseText.trim());
                const today = new Date();
                let cards = '';
                for(let i=0; i<events.length; i++) {
                    cards += '<div class="event-card" data-festival-id="' + events[i].festival_id + '">'

                    const startDate = new Date(events[i].startDate); // startDate가 ISO 문자열 형식이라면 Date로 변환
                    const endDate = new Date(events[i].endDate); // endDate도 마찬가지로 변환

                    let badgeText = '';
                    if (today >= startDate && today <= endDate) {
                        badgeText = '진행중';
                    } else if (today < startDate) {
                        badgeText = '예정';
                    } else if (today > endDate) {
                        badgeText = '종료';
                    }
                    cards += '<div class="event-badge">' + badgeText + '</div>';

                    cards += '<div class="event-image">';
                    cards += '<img src="' + events[i].image + '" alt="' + events[i].title + '" />';
                    cards += '</div>';
                    cards += '<div class="event-info">';
                    cards += '<h3 class="event-title">' + events[i].title + '</h3>';
                    cards += '<div class="learn-more">Learn more →</div>';
                    cards += '</div>';
                    cards += '</div>';
                }
                document.getElementById('eventgrid').innerHTML = cards;

                const efrm = document.getElementById('efrm');
                const hiddenInput = document.getElementById('hiddenFestivalId');

                document.querySelectorAll('.event-card').forEach(card => {
                    card.addEventListener('click', function () {
                        const festivalId = this.getAttribute('data-festival-id');

                        hiddenInput.value = festivalId; // 선택된 festival_id 설정
                        efrm.submit();
                    });
                });
            } else {
                alert(`[에러] 축제 불러오기 요청 실패: ${xhr.status}`)
            }

        }
    }
    xhr.open("GET", "/api/events", true);
    xhr.send();
}


function filterEventsByDate(year, month){
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    const events = JSON.parse(xhr.responseText.trim());
                    let cards = '';
                    for(let i=0; i<events.length; i++) {
                        cards += '<div class="event-card">';
                        cards += '<input type="hidden" name="id" value="' + events[i].id + '">';
                        cards += '<div class="event-badge">진행중</div>';
                        cards += '<div class="event-image">';
                        cards += '<img src="' + events[i].image + '" alt="' + events[i].title + '" />';
                        cards += '</div>';
                        cards += '<div class="event-info">';
                        cards += '<h3 class="event-title">' + events[i].title + '</h3>';
                        cards += '<div class="learn-more">Learn more →</div>';
                        cards += '</div>';
                        cards += '</div>';
                    }
                    document.getElementById('eventgrid').innerHTML = cards;
                } else {
                    alert(`[에러] 날짜별 필터링 요청 실패: ${xhr.status}`)
                }
            }
        }
    const url = `/api/events/filter/month?year=${encodeURIComponent(year)}&month=${encodeURIComponent(month || '')}`;
    xhr.open("Get", url, true);
    xhr.send();
}

function filterEventsByRegion(region) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                const events = JSON.parse(xhr.responseText.trim());
                let cards = '';
                for(let i=0; i<events.length; i++) {
                    cards += '<div class="event-card">';
                    cards += '<input type="hidden" name="id" value="' + events[i].id + '">';
                    cards += '<div class="event-badge">진행중</div>';
                    cards += '<div class="event-image">';
                    cards += '<img src="' + events[i].image + '" alt="' + events[i].title + '" />';
                    cards += '</div>';
                    cards += '<div class="event-info">';
                    cards += '<h3 class="event-title">' + events[i].title + '</h3>';
                    cards += '<div class="learn-more">Learn more →</div>';
                    cards += '</div>';
                    cards += '</div>';
                }
                document.getElementById('eventgrid').innerHTML = cards;
            } else {
                alert(`[에러] 지역별 필터링 요청 실패: ${xhr.status}`)
            }
        }
    };

    // 지역 정보를 URL에 추가
    const url = `/api/events/filter/region?region=${encodeURIComponent(region)}`;
    xhr.open("GET", url, true);
    xhr.send();
}

function searchFestivals() {
    const keyword = document.getElementById('searchBar').value.trim();
    if (keyword === "" ) {
        alert("검색어를 입력해주세요.");
        return;
    }
    console.log(keyword)
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                const events = JSON.parse(xhr.responseText.trim());
                let cards = '';
                for(let i=0; i<events.length; i++) {
                    cards += '<div class="event-card">';
                    cards += '<input type="hidden" name="id" value="' + events[i].id + '">';
                    cards += '<div class="event-badge">진행중</div>';
                    cards += '<div class="event-image">';
                    cards += '<img src="' + events[i].image + '" alt="' + events[i].title + '" />';
                    cards += '</div>';
                    cards += '<div class="event-info">';
                    cards += '<h3 class="event-title">' + events[i].title + '</h3>';
                    cards += '<div class="learn-more">Learn more →</div>';
                    cards += '</div>';
                    cards += '</div>';
                }
                document.getElementById('eventgrid').innerHTML = cards;
            } else {
                alert(`[에러] 검색 요청 실패: ${xhr.status}`)
            }
        }
    };

    // 검색 API 호출
    xhr.open("GET", `/api/events/search?keyword=${keyword}`, true);
    xhr.send();
}
