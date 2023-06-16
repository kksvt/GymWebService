package uj.wmii.jwzp.gymclass.entities;

import uj.wmii.jwzp.common.Gender;

public record GymClassSummaryDTO(Long id, String name, GymClassType type, GymClassLevel level, Gender gender) {

}