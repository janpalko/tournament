package sk.palko.tournament.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ExactValueValidator implements ConstraintValidator<ExactValue, Object> {

  private Object[] validValues;

  @Override
  public void initialize(ExactValue constraintAnnotation) {
    validValues = constraintAnnotation.values();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    return Arrays.stream(validValues)
        .anyMatch(validValue -> validValue.equals(value));
  }

}
