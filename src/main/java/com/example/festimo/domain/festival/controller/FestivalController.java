package com.example.festimo.domain.festival.controller;

import com.example.festimo.domain.festival.dto.FestivalTO;
import com.example.festimo.domain.festival.repository.FestivalRepository;
import com.example.festimo.domain.festival.service.FestivalService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class FestivalController {

    @Value("${KAKAO_MAP_API_KEY}")
    private String KAKAO_MAP_API_KEY;

    @ResponseBody
    @GetMapping("/api/map-key")
    public String getApiKey() {
        return KAKAO_MAP_API_KEY;
    }

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private PagedResourcesAssembler<FestivalTO> pagedResourcesAssembler;

    /*
    @GetMapping("/")
    public String getMain() {
        return "festival.html";
    }
     */

    @ResponseBody
    @GetMapping("/api/events")
    public PagedModel<FestivalTO> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "28") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FestivalTO> paginatedEvent = festivalService.findPaginated(pageable);

        PagedModel<EntityModel<FestivalTO>> pagedModel = pagedResourcesAssembler.toModel(paginatedEvent, festival -> EntityModel.of(festival));
        PagedModel<FestivalTO> pagination = PagedModel.of(pagedModel.getContent().stream()
                .map(entityModel -> entityModel.getContent())
                .collect(Collectors.toList()), pagedModel.getMetadata());
        return pagination;
    }

    @ResponseBody
    @GetMapping("/api/events/{eventId}")
    public FestivalTO getEvent(@PathVariable Integer eventId) {
        FestivalTO to = festivalService.findById(eventId);
        return to;
    }

    @ResponseBody
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

    @ResponseBody
    @GetMapping("/api/events/filter/region")
    public List<FestivalTO> filterByRegion(@RequestParam String region) {
        return festivalService.filterByRegion(region);
    }


}
