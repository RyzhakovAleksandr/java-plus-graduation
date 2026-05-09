package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.CategoryClient;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.constant.Message;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServerImpl implements CompilationServer {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventClient eventClient;
    private final CategoryClient categoryClient;
    private final UserClient userClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info(Message.LOG_GET_COMPILATIONS, pinned, from, size);

        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size);

        return compilations.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info(Message.LOG_GET_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND_COMPILATION));

        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        log.info(Message.LOG_SAVE_COMPILATION, newCompilationDto);

        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new IllegalArgumentException(Message.EXCEPTION_TITLE_CAN_NOT_BLANK);
        }

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        compilation.setPinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEventIds(new HashSet<>(newCompilationDto.getEvents()));
        }

        compilation = compilationRepository.save(compilation);

        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        log.info(Message.LOG_UPDATE_COMPILATION,  compId, updateRequest);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND_COMPILATION));

        compilationMapper.updateCompilationRequestToCompilation(compilation, updateRequest);

        if (updateRequest.getEvents() != null) {
            compilation.setEventIds(new HashSet<>(updateRequest.getEvents()));
        }

        compilation = compilationRepository.save(compilation);

        return toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info(Message.LOG_DELETE_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundException(Message.EXCEPTION_NOT_FOUND_COMPILATION));

        compilationRepository.delete(compilation);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = compilationMapper.compilationToCompilationDto(compilation);

        if (compilation.getEventIds() != null && !compilation.getEventIds().isEmpty()) {
            List<Long> eventIds = new ArrayList<>(compilation.getEventIds());
            List<EventShortDto> events = eventClient.getEventsByIds(eventIds);

            List<EventShortDto> enrichedEvents = enrichEventsWithCategoriesAndUsers(events);
            dto.setEvents(enrichedEvents);
        } else {
            dto.setEvents(List.of());
        }

        return dto;
    }

    private List<EventShortDto> enrichEventsWithCategoriesAndUsers(List<EventShortDto> events) {
        if (events == null || events.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = events.stream()
                .map(EventShortDto::getCategory)
                .filter(Objects::nonNull)
                .map(CategoryDto::getId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> userIds = events.stream()
                .map(EventShortDto::getInitiator)
                .filter(Objects::nonNull)
                .map(UserShortDto::getId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, CategoryDto> categoryMap = new HashMap<>();
        Map<Long, UserShortDto> userMap = new HashMap<>();

        if (!categoryIds.isEmpty()) {
            for (Long id : categoryIds) {
                try {
                    CategoryDto category = categoryClient.getCategory(id);
                    if (category != null) {
                        categoryMap.put(id, category);
                    }
                } catch (Exception e) {
                    log.warn(Message.WARM_CAN_GET_CATEGORY, id, e.getMessage());
                }
            }
        }

        if (!userIds.isEmpty()) {
            for (Long id : userIds) {
                try {
                    UserDto user = userClient.getUser(id);
                    if (user != null) {
                        userMap.put(id, UserShortDto.builder().id(user.getId()).name(user.getName()).build());
                    }
                } catch (Exception e) {
                    log.warn(Message.WARM_CAN_GET_USER, id, e.getMessage());
                }
            }
        }

        return events.stream()
                .map(event -> {
                    if (event.getCategory() != null && event.getCategory().getId() != null) {
                        CategoryDto category = categoryMap.get(event.getCategory().getId());
                        if (category != null) {
                            event.setCategory(category);
                        }
                    }
                    if (event.getInitiator() != null && event.getInitiator().getId() != null) {
                        UserShortDto user = userMap.get(event.getInitiator().getId());
                        if (user != null) {
                            event.setInitiator(user);
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());
    }
}
