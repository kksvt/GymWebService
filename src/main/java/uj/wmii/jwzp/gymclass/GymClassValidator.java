package uj.wmii.jwzp.gymclass;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uj.wmii.jwzp.gymclass.entities.GymClass;

@Component
public class GymClassValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return GymClass.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GymClass gymClass = (GymClass) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "gymClass.name.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "instructor", "gymClass.instructor.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "gymClass.type.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "level", "gymClass.level.empty");
    }
}
