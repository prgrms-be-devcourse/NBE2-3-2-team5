package com.example.festimo.domain.meet.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

import com.example.festimo.domain.meet.dto.LeaderApplicationResponse;
import com.example.festimo.domain.meet.entity.Applications;


@Mapper
public interface LeaderApplicationMapper {

    LeaderApplicationMapper INSTANCE = Mappers.getMapper(LeaderApplicationMapper.class);

    @Mapping(source = "applicationId", target = "applicationId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "appliedDate", target = "appliedDate")
    LeaderApplicationResponse toDto(Applications application);

    List<LeaderApplicationResponse> toDtoList(List<Applications> applications);
}
