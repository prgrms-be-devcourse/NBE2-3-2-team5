package com.example.festimo.domain.festival.service;

import com.example.festimo.domain.festival.domain.Festival;
import com.example.festimo.domain.festival.dto.FestivalDetailsTO;
import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FestivalService {

    @Value("${SEARCH_FESTIVAL_API_KEY}")
    private String SEARCH_FESTIVAL_API_KEY;
    @Value("${INFO_FESTIVAL_API_KEY}")
    private String INFO_FESTIVAL_API_KEY;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleRefreshEvents() {
        refreshEvents();
    }

    public void refreshEvents() {
        // 기존 데이터를 삭제하여 데이터 갱신
        festivalRepository.deleteAll();

        resetAutoIncrement();

        // API 호출로 데이터 가져오기
        List<FestivalTO> events = getAllEvents();

        // 가져온 데이터를 데이터베이스에 저장
        for (FestivalTO event : events) {
            insert(event);
        }
    }

    public List<FestivalTO> getAllEvents(){
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://apis.data.go.kr/B551011/KorService1");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        String baseUrl = "https://apis.data.go.kr/B551011/KorService1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(factory);

        List<FestivalTO> festivalList = new ArrayList<>();
        int pageNo = 1;
        int numOfRows = 100;
        try {
            while(true) {
                String url = factory.expand("/searchFestival1?serviceKey={serviceKey}&eventStartDate={eventStartDate}" +
                                "&pageNo={pageNo}&numOfRows={numOfRows}&MobileApp={MobileApp}" +
                                "&MobileOS={MobileOS}&listYN={listYN}&arrange={arrange}&_type={_type}",
                        Map.of(
                                "serviceKey", SEARCH_FESTIVAL_API_KEY,
                                "eventStartDate", "20230729",
                                "pageNo", pageNo,
                                "numOfRows", numOfRows,
                                "MobileApp", "AppTest",
                                "MobileOS", "ETC",
                                "listYN", "Y",
                                "arrange", "O",
                                "_type", "json"
                        )).toString();
                URI uri = new URI(url);
                ResponseEntity<Map> responseEntity = restTemplate.getForEntity(uri, Map.class);
                Map<String, Object> response = responseEntity.getBody();

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

                    festivalTO.setContentId(Integer.parseInt((String) item.get("contentid")));

                    festivalList.add(festivalTO);
                }
                int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                if(pageNo >= totalPages){
                    break;
                }
                pageNo++;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return festivalList;
    }

    private FestivalDetailsTO getFestivalDescription(int contentId) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://apis.data.go.kr/B551011/KorService1");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        String baseUrl = "https://apis.data.go.kr/B551011/KorService1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(factory);

        FestivalDetailsTO details = new FestivalDetailsTO();
        try{
            String url = factory.expand("/detailInfo1?serviceKey={serviceKey}&MobileApp={MobileApp}" +
                            "&MobileOS={MobileOS}&contentId={contentId}&contentTypeId={contentTypeId}&_type={_type}",
                    Map.of(
                            "serviceKey", INFO_FESTIVAL_API_KEY,
                            "MobileApp", "AppTest",
                            "MobileOS", "ETC",
                            "contentId", contentId,
                            "contentTypeId", "15",
                            "_type", "json"
                    )).toString();
            URI uri = new URI(url);
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(uri, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            // 결과 확인
            Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) response.get("response")).get("body");
            Object items = body.get("items");

            if (items instanceof Map) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) ((Map<String, Object>) items).get("item");
                if (itemList != null) {
                    for (Map<String, Object> item : itemList) {
                        String infoName = (String) item.get("infoname");
                        String infoText = (String) item.get("infotext");
                        details.getDetails().add(new FestivalDetailsTO.Detail(infoName, infoText));
                    }
                }
            } else if (items instanceof List) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items;
                for (Map<String, Object> item : itemList) {
                    String infoName = (String) item.get("infoname");
                    String infoText = (String) item.get("infotext");
                    details.getDetails().add(new FestivalDetailsTO.Detail(infoName, infoText));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching description: " + e.getMessage());
        }
        return details;
    }

    private void resetAutoIncrement() {
        // SQL 쿼리를 실행해 시퀀스 초기화
        String resetQuery = "ALTER TABLE festival AUTO_INCREMENT = 1";
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery(resetQuery).executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
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

    public Page<FestivalTO> findPaginated(Pageable pageable) {
        Page<Festival> festivals = festivalRepository.findAll(pageable);
        ModelMapper modelMapper = new ModelMapper();
        Page<FestivalTO> page = festivals.map(festival -> modelMapper.map(festival, FestivalTO.class));
        return page;
    }

    public FestivalTO findById(int id){
        Festival festival = festivalRepository.findById(String.valueOf(id)).orElse(null);
        if (festival == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FestivalTO to = modelMapper.map(festival, FestivalTO.class);

        int contentId = festival.getContentId();
        FestivalDetailsTO details = getFestivalDescription(contentId);

        to.setFestivalDetails(details);

        return to;
    }

    public List<FestivalTO> search(String keyword) {
        List<Festival> festivalList = festivalRepository.findByTitleContainingIgnoreCase(keyword);

        ModelMapper modelMapper = new ModelMapper();
        List<FestivalTO> list = festivalList.stream()
                .map(p -> modelMapper.map(p, FestivalTO.class))
                .collect(Collectors.toList());
        return list;
    }

    public List<FestivalTO> filterByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        ModelMapper modelMapper = new ModelMapper();
        List<FestivalTO> list = festivalRepository.findAll().stream()
                .filter(festival -> isFestivalInMonth(festival, firstDayOfMonth, lastDayOfMonth))
                .map(festival -> modelMapper.map(festival, FestivalTO.class))
                .collect(Collectors.toList());
        return list;
    }

    private boolean isFestivalInMonth(Festival festival, LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        LocalDate startDate = festival.getStartDate();
        LocalDate endDate = festival.getEndDate();

        return !startDate.isAfter(lastDayOfMonth) && !endDate.isBefore(firstDayOfMonth);
    }

    public List<FestivalTO> filterByRegion(String region) {
        List<Festival> festivals = festivalRepository.findByAddressContainingIgnoreCase(region);

        ModelMapper modelMapper = new ModelMapper();
        List<FestivalTO> list = festivals.stream()
                .map(festival -> modelMapper.map(festival, FestivalTO.class))
                .collect(Collectors.toList());
        return list;
    }
}

