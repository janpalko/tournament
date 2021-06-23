package sk.palko.tournament.util;

import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;

public final class MatchData {

  public static Match createMatch(Player firstPlayer, Player secondPlayer) {
    Match match = new Match();
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    return match;
  }

  public static Match createMatchWithId(int id, Player firstPlayer, Player secondPlayer) {
    Match match = createMatch(firstPlayer, secondPlayer);
    match.setMatchId(id);
    return match;
  }

}
