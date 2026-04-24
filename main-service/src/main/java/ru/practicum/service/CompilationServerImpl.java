package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.exception.NotFoundCompletion;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.mapper.CompilationMapper;
import main.java.ru.practicum.mapper.EventMapper;
import main.java.ru.practicum.mapper.UserMapper;
import main.java.ru.practicum.persistence.entity.Compilation;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.repository.CategoryRepository;
import main.java.ru.practicum.persistence.repository.CompilationRepository;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.openapi.model.CategoryDto;
import ru.practicum.openapi.model.CompilationDto;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewCompilationDto;
import ru.practicum.openapi.model.UpdateCompilationRequest;
import ru.practicum.openapi.model.UserShortDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServerImpl implements main.java.ru.practicum.service.CompilationServer {
    private final CompilationRepository compilationRepository;

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final UserRepository initiatorRepository;

    private final CompilationMapper compilationMapper;

    private final EventMapper eventMapper;

    private final CategoryMapper categoryMapper;

    private final UserMapper initiatorMapper;

    @Override
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info(Messages.GET_COMPILATIONS);

        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size);
        List<CompilationDto> list = compilations.stream()
                .map(c -> compilationMapper.compilationToCompilationDto(c)
                        .events(completionCompilationDto(c.getEvents()))).toList();

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilation(Long compId) {
        log.info(Messages.GET_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(compilation.getEvents()));

        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto newCompilationDto) {
        log.info(Messages.SAVE_COMPILATION, newCompilationDto);

        if (newCompilationDto.getTitle().isBlank()) {
            throw new IllegalArgumentException();
        }

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        compilation.setEvents(new HashSet<>(
                eventRepository.getEventsByIds(newCompilationDto.getEvents().toArray(Long[]::new))));
        compilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(compilation.getEvents()));

        return ResponseEntity.status(HttpStatus.CREATED).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info(Messages.UPDATE_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));
        compilationMapper.updateCompilationRequestToCompilation(compilation, updateCompilationRequest);

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(new HashSet<>(
                    eventRepository.getEventsByIds(updateCompilationRequest.getEvents().toArray(Long[]::new))));
        }

        compilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(compilation.getEvents()));

        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteCompilation(Long compId) {
        log.info(Messages.DELETE_COMPILATION, compId);

        compilationRepository.deleteById(compId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private List<CategoryDto> getCategories(Long[] categoryIds) {
        return categoryRepository.getCategoriesByIds(categoryIds).stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    private List<UserShortDto> getUsers(Long[] initiatorIds) {
        return initiatorRepository.getInitiatorByCompilationIds(initiatorIds).stream()
                .map(initiatorMapper::userToUserShortDto)
                .toList();
    }

    private List<EventShortDto> completionCompilationDto(Collection<Event> events) {
        List<CategoryDto> categories = getCategories(events.stream()
                .map(Event::getCategory).distinct().toArray(Long[]::new));
        List<UserShortDto> users = getUsers(events.stream()
                .map(Event::getInitiator).distinct().toArray(Long[]::new));

        return events.stream().map(e -> eventMapper.eventToEventShortDto(e)
                        .category(categories.stream().filter(ct -> ct.getId()
                                .equals(e.getCategory())).findFirst().get())
                        .initiator(users.stream().filter(u -> u.getId()
                                .equals(e.getInitiator())).findFirst().get()))
                .toList();
    }
}
