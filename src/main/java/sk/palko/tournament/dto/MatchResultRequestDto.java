package sk.palko.tournament.dto;

import sk.palko.tournament.validation.ExactValue;

public record MatchResultRequestDto(
    @ExactValue(values = {"2:0", "2:1", "0:2", "1:2"}, message = "Result should be either 2:0, 2:1, 0:2 or 1:2") String result) {
}
