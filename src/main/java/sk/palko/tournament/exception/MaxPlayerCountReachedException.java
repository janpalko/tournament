package sk.palko.tournament.exception;

import org.springframework.http.HttpStatus;

public class MaxPlayerCountReachedException extends RestException {

  public MaxPlayerCountReachedException(int maxPlayerCount) {
    super(String.format("Unable to create a player. Max. player count '%d' has been reached.", maxPlayerCount),
        HttpStatus.BAD_REQUEST);
  }

}
