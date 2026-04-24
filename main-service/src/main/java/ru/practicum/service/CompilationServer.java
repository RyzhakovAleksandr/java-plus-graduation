package main.java.ru.practicum.service;

import org.springframework.http.ResponseEntity;

import ru.practicum.openapi.model.CompilationDto;
import ru.practicum.openapi.model.NewCompilationDto;
import ru.practicum.openapi.model.UpdateCompilationRequest;

import java.util.List;

public interface CompilationServer {
    ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<CompilationDto> getCompilation(Long compId);

    ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto newCompilationDto);

    ResponseEntity<Void> deleteCompilation(Long compId);

    ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
