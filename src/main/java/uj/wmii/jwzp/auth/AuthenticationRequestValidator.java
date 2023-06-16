package uj.wmii.jwzp.auth;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
@Component
public class AuthenticationRequestValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return AuthenticationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthenticationRequest authenticationRequest = (AuthenticationRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "authenticationRequest.email.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "authenticationRequest.password.empty");
    }
}