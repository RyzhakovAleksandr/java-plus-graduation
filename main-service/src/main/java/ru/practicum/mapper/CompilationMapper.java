package main.java.ru.practicum.mapper;

import main.java.ru.practicum.persistence.entity.Compilation;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.openapi.model.CompilationDto;
import ru.practicum.openapi.model.NewCompilationDto;
import ru.practicum.openapi.model.UpdateCompilationRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", ignore = true)
    void updateCompilationRequestToCompilation(@MappingTarget Compilation compilation,
                                               UpdateCompilationRequest updateCompilationRequest);
}
