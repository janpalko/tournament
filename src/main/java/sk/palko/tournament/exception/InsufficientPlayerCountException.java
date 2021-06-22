package sk.palko.tournament.exception;

import org.springframework.http.HttpStatus;

public class InsufficientPlayerCountException extends RestException {

  public InsufficientPlayerCountException(int requiredPlayerCount, long currentPlayerCount) {
    super(String.format("Unable to create tournament draw. '%d' players are required, but got only '%d'.",
        requiredPlayerCount, currentPlayerCount), HttpStatus.PRECONDITION_FAILED);
  }

}
