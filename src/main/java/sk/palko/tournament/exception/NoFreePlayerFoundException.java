package sk.palko.tournament.exception;

import org.springframework.http.HttpStatus;

public class NoFreePlayerFoundException extends RestException {

  public NoFreePlayerFoundException() {
    super(String.format("No free player found to create a match"), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
