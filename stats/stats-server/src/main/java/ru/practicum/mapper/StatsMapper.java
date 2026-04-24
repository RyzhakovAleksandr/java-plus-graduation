package ru.practicum.mapper;

import org.mapstruct.Mapper;

import ru.practicum.model.EndpointHit;

import dto.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto hitDto);
}
