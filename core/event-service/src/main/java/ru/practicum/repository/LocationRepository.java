package ru.practicum.repository;

import ru.practicum.model.LocationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
