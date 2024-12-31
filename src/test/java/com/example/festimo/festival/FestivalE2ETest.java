package com.example.festimo.festival;

import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.service.FestivalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FestivalE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FestivalService festivalService;

    @BeforeEach
    void setUp() {
        FestivalTO festivalTO1 = new FestivalTO();
        festivalTO1.setTitle("축제1");
        festivalTO1.setAddress("서울");
        festivalTO1.setCategory("축제");
        festivalTO1.setStartDate(LocalDate.of(2024, 1, 5));
        festivalTO1.setEndDate(LocalDate.of(2024, 1, 10));
        festivalTO1.setXCoordinate(37.5665f);
        festivalTO1.setYCoordinate(126.9780f);

        FestivalTO festivalTO2 = new FestivalTO();
        festivalTO2.setTitle("축제2");
        festivalTO2.setAddress("제주");
        festivalTO2.setCategory("축제");
        festivalTO2.setStartDate(LocalDate.of(2024, 2, 15));
        festivalTO2.setEndDate(LocalDate.of(2024, 2, 20));
        festivalTO2.setXCoordinate(33.4996f);
        festivalTO2.setYCoordinate(126.5312f);

        FestivalTO festivalTO3 = new FestivalTO();
        festivalTO3.setTitle("축제3");
        festivalTO3.setAddress("경기");
        festivalTO3.setCategory("행사");
        festivalTO3.setStartDate(LocalDate.of(2024, 3, 1));
        festivalTO3.setEndDate(LocalDate.of(2024, 3, 5));
        festivalTO3.setXCoordinate(37.4138f);
        festivalTO3.setYCoordinate(127.5183f);

        FestivalTO festivalTO4 = new FestivalTO();
        festivalTO4.setTitle("축제3");
        festivalTO4.setAddress("강원");
        festivalTO4.setCategory("축제");
        festivalTO4.setStartDate(LocalDate.of(2024, 4, 10));
        festivalTO4.setEndDate(LocalDate.of(2024, 4, 15));
        festivalTO4.setXCoordinate(37.8228f);
        festivalTO4.setYCoordinate(128.1555f);

        festivalService.resetAutoIncrement();
        festivalService.insert(festivalTO1);
        festivalService.insert(festivalTO2);
        festivalService.insert(festivalTO3);
        festivalService.insert(festivalTO4);
    }

    @Test
    void testGetAllEvents() throws Exception {
        mockMvc.perform(get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.festivalTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.festivalTOList.length()").value(equalTo(4)));
    }

    @Test
        void testGetEventById() throws Exception {
            mockMvc.perform(get("/api/events/{eventId}", 1)
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("축제1")))
                    .andExpect(jsonPath("$.address", is("서울")))
                    .andExpect(jsonPath("$.category", is("축제")))
                    .andExpect(jsonPath("$.startDate", is("2024-01-05")))
                    .andExpect(jsonPath("$.endDate", is("2024-01-10")))
                    .andExpect(jsonPath("$.xcoordinate", is(37.5665)))
                    .andExpect(jsonPath("$.ycoordinate", is(126.9780)));


        mockMvc.perform(get("/api/events/{eventId}", 3)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("축제3")))
                .andExpect(jsonPath("$.address", is("경기")));
    }

    @Test
    void testSearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/events/search")
                        .param("keyword", "축제1")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].title", is("축제1")));
    }

    @Test
    void testFilterByMonth() throws Exception {
        mockMvc.perform(get("/api/events/filter/month")
                        .param("year", "2024") // 필터링 연도 설정
                        .param("month", "1")  // 필터링 월 설정
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[*].title", hasItem("축제1")));
    }

    @Test
    void testFilterByRegion() throws Exception {
        mockMvc.perform(get("/api/events/filter/region")
                        .param("region", "서울")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", hasItem("축제1")));
    }
}

