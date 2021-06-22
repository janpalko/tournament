package sk.palko.tournament.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorMessageDto(int status, HttpStatus error, List<String> messages) {

  public ErrorMessageDto(int status, HttpStatus error, String message) {
    this(status, error, List.of(message));
  }

}
