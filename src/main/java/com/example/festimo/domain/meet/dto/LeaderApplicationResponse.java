package com.example.festimo.domain.meet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderApplicationResponse {
    private Long userId;
    private String nickname;
    private String gender;
    private Double ratingAvg;
}
