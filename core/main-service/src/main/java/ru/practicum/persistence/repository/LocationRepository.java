package ru.practicum.persistence.repository;

import ru.practicum.persistence.entity.LocationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
