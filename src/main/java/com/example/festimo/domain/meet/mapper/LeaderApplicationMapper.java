package com.example.festimo.domain.meet.mapper;

import com.example.festimo.domain.meet.dto.ApplicateUsersProjection;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

import com.example.festimo.domain.meet.dto.LeaderApplicationResponse;
import com.example.festimo.domain.meet.entity.Applications;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface LeaderApplicationMapper {
    LeaderApplicationMapper INSTANCE = Mappers.getMapper(LeaderApplicationMapper.class);

    LeaderApplicationResponse toDto(ApplicateUsersProjection projection);

    List<LeaderApplicationResponse> toDtoList(List<ApplicateUsersProjection> projections);
}
