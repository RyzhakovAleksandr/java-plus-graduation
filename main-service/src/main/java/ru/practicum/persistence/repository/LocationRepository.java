package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.LocationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
