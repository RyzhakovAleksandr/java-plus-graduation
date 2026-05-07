package ru.practicum.mapper;

import ru.practicum.model.LocationEntity;

import org.mapstruct.Mapper;

import ru.practicum.dto.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationEntity locationToLocationEntity(Location location);

    Location locationEntityToLocation(LocationEntity location);
}
