let currentPage = 0;
const pageSize = 28;

window.onload = function () {
    loadEvents(currentPage);
};

function loadEvents(page) {
    currentPage = page;
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if ( xhr.readyState == 4 ) {
            if ( xhr.status == 200 ) {
                const response = JSON.parse(xhr.responseText.trim());
                const events = response._embedded ? response._embedded.festivalTOList : [];
                const totalPages = response.page.totalPages;

                renderEvents(events);
                renderPagination(totalPages, page);

            } else {
                alert(`[에러] 축제 불러오기 요청 실패: ${xhr.status}`)
            }

        }
    }
    const url = `/api/events?page=${page}&size=${pageSize}`;
    xhr.open("GET", url, true);
    xhr.send();
}

function renderEvents(events) {
    const today = new Date();
    let cards = '';
    for(let i=0; i<events.length; i++) {
        cards += '<div class="event-card" data-festival-id="' + events[i].festival_id + '">'

        const startDate = new Date(events[i].startDate);
        const endDate = new Date(events[i].endDate);

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
}

function renderPagination(totalPages, currentPage) {
    let paginationHTML = '';
    const pagesPerGroup = 10;
    const currentGroup = Math.floor(currentPage / pagesPerGroup);
    const startPage = currentGroup * pagesPerGroup;
    const endPage = Math.min(startPage + pagesPerGroup - 1, totalPages - 1);


    paginationHTML += `<a href="#" class="page-number" onclick="loadEvents(0); return false;">&lt&lt;</a>`;
    if (currentGroup > 0) {
        paginationHTML += `<a href="#" class="page-number" onclick="loadEvents(${startPage - 1}); return false;">&lt;</a>`;
    }

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `<a href="#" class="page-number ${i === currentPage ? 'active' : ''}" onclick="loadEvents(${i}); return false;">${i + 1}</a>`;
    }

    if (endPage < totalPages - 1) {
        paginationHTML += `<a href="#" class="page-number" onclick="loadEvents(${endPage + 1}); return false;">&gt;</a>`;
    }
    paginationHTML += `<a href="#" class="page-number" onclick="loadEvents(${totalPages - 1}); return false;">&gt&gt;</a>`;

    document.querySelector('.pagination').innerHTML = paginationHTML;
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
