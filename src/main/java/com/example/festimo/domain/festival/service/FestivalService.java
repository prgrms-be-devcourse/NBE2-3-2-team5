package com.example.festimo.domain.festival.service;

import com.example.festimo.domain.festival.domain.Festival;
import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FestivalService {

    @Value("${SEARCH_FESTIVAL_API_KEY}")
    private String SEARCH_FESTIVAL_API_KEY;

    @Autowired
    private FestivalRepository festivalRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleRefreshEvents() {
        refreshEvents();
    }

    public void refreshEvents() {
        // 기존 데이터를 삭제하여 데이터 갱신
        festivalRepository.deleteAll();

        // API 호출로 데이터 가져오기
        List<FestivalTO> events = getAllEvents();

        // 가져온 데이터를 데이터베이스에 저장
        for (FestivalTO event : events) {
            insert(event);
        }
    }

    public List<FestivalTO> getAllEvents(){
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://apis.data.go.kr/B551011/KorService1");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        // webClient 기본 설정
        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl("https://apis.data.go.kr/B551011/KorService1")
                .defaultHeader("Accept", "application/json")
                .build();

        List<FestivalTO> festivalList = new ArrayList<>();
        AtomicInteger pageNo = new AtomicInteger(1);
        int numOfRows = 100;
        try {
            while(true) {
                // API 요청
                Map<String, Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/searchFestival1")
                                .queryParam("serviceKey", SEARCH_FESTIVAL_API_KEY)
                                .queryParam("eventStartDate", "20230729")
                                .queryParam("pageNo", pageNo.get())
                                .queryParam("numOfRows", numOfRows)
                                .queryParam("MobileApp", "AppTest")
                                .queryParam("MobileOS", "ETC")
                                .queryParam("listYN", "Y")
                                .queryParam("arrange", "O")
                                .queryParam("_type", "json")
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                // 결과 확인
                // JSON 데이터에서 필요한 정보 추출
                Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) response.get("response")).get("body");
                int totalCount = (int) body.get("totalCount");
                Map<String, Object> items = (Map<String, Object>) body.get("items");

                // 데이터가 없으면 반복 종료
                if (items == null || !items.containsKey("item")) {
                    break;
                }

                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                for (Map<String, Object> item : itemList) {
                    FestivalTO festivalTO = new FestivalTO();
                    festivalTO.setTitle((String) item.get("title"));
                    festivalTO.setAddress((String) item.get("addr1") + " " + (String) item.get("addr2"));

                    String cat2 = (String) item.get("cat2");
                    String category;
                    if ("A0207".equals(cat2)) {
                        category = "축제";
                    } else if ("A0208".equals(cat2)) {
                        category = "행사";
                    } else {
                        category = "기타";
                    }

                    festivalTO.setCategory(category);
                    festivalTO.setStartDate(LocalDate.parse((CharSequence) item.get("eventstartdate"), formatter));
                    festivalTO.setEndDate(LocalDate.parse((CharSequence) item.get("eventenddate"), formatter));
                    festivalTO.setImage((String) item.get("firstimage"));
                    festivalTO.setXCoordinate(Float.parseFloat((String) item.get("mapx")));
                    festivalTO.setYCoordinate(Float.parseFloat((String) item.get("mapy")));
                    festivalTO.setPhone((String) item.get("tel"));
                    festivalTO.setDescription("description");
                    festivalList.add(festivalTO);
                }
                int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                if(pageNo.get() >= totalPages){
                    break;
                }
                pageNo.incrementAndGet();
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error(" + e.getStatusCode() + "): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return festivalList;
    }

    public void insert(FestivalTO to){
        ModelMapper modelMapper = new ModelMapper();
        Festival festival = modelMapper.map(to, Festival.class);

        festivalRepository.save(festival);
    }

    public List<FestivalTO> findAll(){
        List<Festival> festivalList = festivalRepository.findAll();

        ModelMapper modelMapper = new ModelMapper();
        List<FestivalTO> list = festivalList.stream()
                .map(p -> modelMapper.map(p, FestivalTO.class))
                .collect(Collectors.toList());

        return list;
    }

    public FestivalTO findById(int id){
        Festival festival = festivalRepository.findById(String.valueOf(id)).orElse(null);
        if (festival == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FestivalTO to = modelMapper.map(festival, FestivalTO.class);

        return to;
    }
}

