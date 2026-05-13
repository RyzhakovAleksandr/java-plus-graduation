package ru.practicum.mapper;

import org.mapstruct.Mapping;
import ru.practicum.model.Compilation;

import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    CompilationDto compilationToCompilationDto(Compilation compilation);

    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

    void updateCompilationRequestToCompilation(@MappingTarget Compilation compilation,
                                               UpdateCompilationRequest updateCompilationRequest);
}
