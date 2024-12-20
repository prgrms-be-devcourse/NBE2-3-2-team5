package com.example.festimo.domain.festival.controller;

import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import com.example.festimo.domain.festival.service.FestivalService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class FestivalController {

    @Value("${KAKAO_MAP_API_KEY}")
    private String KAKAO_MAP_API_KEY;

    @GetMapping("/api/map-key")
    public String getApiKey() {
        return KAKAO_MAP_API_KEY;
    }


    @Autowired
    private FestivalService festivalService;

    /*
    @GetMapping("/")
    public String getMain() {
        return "festival.html";
    }
     */

    @ResponseBody
    @GetMapping("/api/events")
    public List<FestivalTO> getAllEvents() {
        // schedule이 아닌 수동으로 확인할 때
        // festivalService.refreshEvents();

        List<FestivalTO> events = festivalService.findAll();
        return events;
    }

    @GetMapping("/api/events/{eventId}")
    public String getEvent(@PathVariable Integer eventId) {
        FestivalTO to = festivalService.findById(eventId);
        return to.toString();
    }

    @GetMapping("/api/events/search")
    public List<FestivalTO> search(@RequestParam String keyword) {
        return festivalService.search(keyword);
    }

    @ResponseBody
    @GetMapping("/api/events/filter/month")
    public List<FestivalTO> filterByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return festivalService.filterByMonth(year, month);
    }

    @GetMapping("/api/events/filter/region")
    public List<FestivalTO> filterByRegion(@RequestParam String region) {
        return festivalService.filterByRegion(region);
    }
}
