package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.constant.Message;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.CompilationServer;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationServer compilationService;

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.GET_COMPILATIONS, pinned, from, size);
        List<CompilationDto> result = compilationService.getCompilations(pinned, from, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        log.info(Message.GET_COMPILATION, compId);
        CompilationDto result = compilationService.getCompilation(compId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationDto> saveCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info(Message.SAVE_COMPILATION);
        CompilationDto result = compilationService.saveCompilation(newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info(Message.UPDATE_COMPILATION, compId);
        CompilationDto result = compilationService.updateCompilation(compId, updateCompilationRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        log.info(Message.DELETE_COMPILATION, compId);
        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }
}
