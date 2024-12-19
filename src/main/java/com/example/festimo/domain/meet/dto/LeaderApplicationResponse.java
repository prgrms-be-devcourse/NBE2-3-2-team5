package com.example.festimo.domain.meet.dto;

import com.example.festimo.domain.meet.entity.Applications;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LeaderApplicationResponse {
    private Long applicationId;
    private Long userId;
    private Applications.Status status;
    private LocalDateTime appliedDate;
}
