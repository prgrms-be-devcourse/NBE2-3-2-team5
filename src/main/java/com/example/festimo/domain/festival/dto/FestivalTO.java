package com.example.festimo.domain.festival.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
public class FestivalTO {
    private int festival_id;
    private String title;
    private String category;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private String image;
    private Float xCoordinate;
    private Float yCoordinate;
    private String phone;
}
