package sk.palko.tournament;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.palko.tournament.controller.PlayerController;
import sk.palko.tournament.controller.TournamentController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTest {

  @Autowired
  private PlayerController playerController;

  @Autowired
  private TournamentController tournamentController;

  @Test
  public void contextLoads() {
    // Then
    assertThat(playerController).isNotNull();
    assertThat(tournamentController).isNotNull();
  }

}
