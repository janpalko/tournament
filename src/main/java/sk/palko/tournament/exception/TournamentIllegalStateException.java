package sk.palko.tournament.exception;

import org.springframework.http.HttpStatus;

public class TournamentIllegalStateException extends RestException {

  public TournamentIllegalStateException(String message) {
    super(message, HttpStatus.PRECONDITION_FAILED);
  }

}
