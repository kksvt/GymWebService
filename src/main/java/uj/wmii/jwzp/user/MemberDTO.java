package uj.wmii.jwzp.user;

import uj.wmii.jwzp.gymclass.entities.GymClass;
import java.util.Set;

public record MemberDTO(long id, String firstName, String lastName, Set<GymClass> classesTaught) {

}
