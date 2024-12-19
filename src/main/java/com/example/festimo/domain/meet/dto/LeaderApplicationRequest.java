package com.example.festimo.domain.meet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LeaderApplicationRequest {
    Long companyId;
    Long LeaderId;
}
