package uj.wmii.jwzp.gymclass.entities;

import uj.wmii.jwzp.common.Gender;

public record GymClassRequest(String name, String description, String instructor, GymClassType type, GymClassLevel level, Gender gender) {
}
