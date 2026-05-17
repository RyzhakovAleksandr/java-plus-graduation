package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "user_actions")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    ActionType actionType;

    @Column(name = "last_action_time", nullable = false)
    Instant lastActionTime;
}
