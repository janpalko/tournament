package sk.palko.tournament.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PlayerRequestDto(
    @NotBlank(message = "Name should not be blank") String name,

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 130, message = "Age should not be greater than 130") int age) {
}
