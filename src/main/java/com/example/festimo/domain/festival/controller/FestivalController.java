package com.example.festimo.domain.festival.controller;

import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import com.example.festimo.domain.festival.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    @RequestMapping("/api/events")
    public String getAllEvents() {
        // schedule이 아닌 수동으로 확인할 때
        // festivalService.refreshEvents();

        List<FestivalTO> events = festivalService.findAll();
        for(FestivalTO to : events) {
            System.out.println(to.getTitle());
        }
        return "getAllFestivals";
    }

    @RequestMapping("/api/events/{eventId}")
    public String getEvent(@PathVariable Integer eventId) {
        FestivalTO to = festivalService.findById(eventId);
        return to.toString();
    }

}
