package sk.palko.tournament.util;

import sk.palko.tournament.domain.Player;

public final class PlayerData {

  public static Player createPlayer(int i) {
    Player player = new Player();
    player.setName("Name " + i);
    player.setAge(i);
    player.setPoints(i);
    return player;
  }

  public static Player createPlayerWithId(int i) {
    Player player = createPlayer(i);
    player.setPlayerId(i);
    return player;
  }



}
