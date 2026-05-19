package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarityEntity;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SimilarityMapper {

    @Mapping(source = "timestamp", target = "updatedAt", qualifiedByName = "longToInstant")
    @Mapping(target = "id", ignore = true)
    EventSimilarityEntity toEntity(EventSimilarityAvro avro);

    @Mapping(source = "timestamp", target = "updatedAt", qualifiedByName = "longToInstant")
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget EventSimilarityEntity entity, EventSimilarityAvro avro);

    @Named("longToInstant")
    default Instant longToInstant(long timestamp) {
        return Instant.ofEpochMilli(timestamp);
    }
}
