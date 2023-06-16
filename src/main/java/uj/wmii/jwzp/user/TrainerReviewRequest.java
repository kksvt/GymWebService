package uj.wmii.jwzp.user;

import org.springframework.lang.NonNull;

public record TrainerReviewRequest(@NonNull Short rating, String ratingDesc) {
}
