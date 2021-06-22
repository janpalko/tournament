package sk.palko.tournament.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ExactValueValidator.class)
public @interface ExactValue {

  String[] values();

  String message() default "{ExactValue.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
