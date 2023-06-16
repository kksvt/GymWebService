package uj.wmii.jwzp.auth;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
@Component
public class RegisterRequestValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequest registerRequest = (RegisterRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "registerRequest.firstName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "registerRequest.lastName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "registerRequest.email.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "membershipDateStart", "registerRequest.dateStart.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "registerRequest.password.empty");
    }
}