package ru.practicum.mapper;

import ru.practicum.persistence.entity.LocationEntity;

import org.mapstruct.Mapper;

import ru.practicum.openapi.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationEntity locationToLocationEntity(Location location);

    Location locationEntityToLocation(LocationEntity location);
}
