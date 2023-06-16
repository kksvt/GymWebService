package uj.wmii.jwzp.gymclass.entities;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record GymClassInstanceRequest(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
}
