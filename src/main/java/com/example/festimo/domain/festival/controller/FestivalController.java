package com.example.festimo.domain.festival.controller;

import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import com.example.festimo.domain.festival.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    @GetMapping("/api/events")
    public String getAllEvents() {
        // schedule이 아닌 수동으로 확인할 때
        // festivalService.refreshEvents();

        List<FestivalTO> events = festivalService.findAll();
        for(FestivalTO to : events) {
            System.out.println(to.getTitle());
        }
        return "getAllFestivals";
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
