window.onload = function() {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if ( xhr.readyState == 4 ) {
            if ( xhr.status == 200 ) {
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
                alert("[에러] 페이지 오류(404,500)")
            }

        }
    }
    xhr.open("GET", "/api/events", true);
    xhr.send();
}