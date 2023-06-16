package uj.wmii.jwzp.auth;

import java.time.LocalDate;

public record RegisterRequest(String firstName, String lastName, String email, String password, LocalDate membershipDateStart){

}
