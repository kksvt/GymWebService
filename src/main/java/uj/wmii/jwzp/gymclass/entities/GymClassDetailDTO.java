package uj.wmii.jwzp.gymclass.entities;

import uj.wmii.jwzp.common.Gender;
import uj.wmii.jwzp.user.MemberDTO;

import java.util.Set;

public record GymClassDetailDTO(
        Long id, String name, String description, MemberDTO instructor, GymClassType type,
        GymClassLevel level, Gender gender, Set<GymClassDate> dates
    ) { //hides members

}
