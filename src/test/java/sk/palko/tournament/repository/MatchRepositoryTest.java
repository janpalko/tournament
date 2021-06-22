package sk.palko.tournament.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.util.MatchData;
import sk.palko.tournament.util.PlayerData;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MatchRepositoryTest {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Test
  public void testCountByResultIsNull() {
    // Given
    Player player1 = PlayerData.createPlayer(1);
    Player player2 = PlayerData.createPlayer(2);
    Player player3 = PlayerData.createPlayer(3);
    playerRepository.saveAll(Arrays.asList(player1, player2, player3));

    Match match1 = MatchData.createMatch(player1, player2);
    match1.setResult("2:0");
    Match match2 = MatchData.createMatch(player1, player3);
    matchRepository.saveAll(Arrays.asList(match1, match2));

    // When
    long result = matchRepository.countByResultIsNull();

    // Then
    assertThat(result).isEqualTo(1);
  }

}
