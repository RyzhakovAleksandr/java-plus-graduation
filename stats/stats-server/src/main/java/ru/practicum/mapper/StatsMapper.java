package main.java.ru.practicum.mapper;

import org.mapstruct.Mapper;

import main.java.ru.practicum.model.EndpointHit;

import dto.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto hitDto);
}
