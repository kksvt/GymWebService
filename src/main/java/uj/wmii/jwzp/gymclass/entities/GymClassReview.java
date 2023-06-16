package uj.wmii.jwzp.gymclass.entities;

import org.springframework.lang.NonNull;

public record GymClassReview(@NonNull Short rating, String ratingDesc) {
}
