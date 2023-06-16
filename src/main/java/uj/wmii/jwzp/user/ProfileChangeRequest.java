package uj.wmii.jwzp.user;

import java.time.LocalDate;

public record ProfileChangeRequest(String firstName, String lastName, String password, LocalDate membershipDateEnd) {
}
