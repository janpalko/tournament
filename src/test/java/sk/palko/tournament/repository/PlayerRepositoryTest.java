package sk.palko.tournament.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.util.PlayerData;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PlayerRepositoryTest {

  @Autowired
  private PlayerRepository playerRepository;

  @Test
  public void testFindAllWithMaxPoints() {
    // Given
    Player player1 = PlayerData.createPlayer(1);
    Player player2 = PlayerData.createPlayer(2);
    Player player3 = PlayerData.createPlayer(3);
    player3.setPoints(2);
    playerRepository.saveAll(Arrays.asList(player1, player2, player3));

    // When
    List<Player> result = (List<Player>) playerRepository.findAllWithMaxPoints();

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).containsExactlyInAnyOrder(player2, player3);
  }

}
