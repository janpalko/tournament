package sk.palko.tournament.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.exception.TournamentIllegalStateException;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;
import sk.palko.tournament.util.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sk.palko.tournament.service.MatchServiceImpl.MATCH_COUNT_PER_PLAYER;
import static sk.palko.tournament.service.PlayerServiceImpl.PLAYER_COUNT;

@ExtendWith(SpringExtension.class)
@Import(MatchServiceImpl.class)
public class MatchServiceImplTest {

  @Autowired
  private MatchService matchService;

  @MockBean
  private MatchRepository matchRepository;
  @MockBean
  private PlayerRepository playerRepository;

  @Test
  public void testListDraw() {
    // Given
    List<Player> players = IntStream.rangeClosed(1, PLAYER_COUNT)
        .mapToObj(PlayerData::createPlayerWithId)
        .collect(Collectors.toList());
    when(playerRepository.count()).thenReturn(Long.valueOf(players.size()));
    when(playerRepository.findAll()).thenReturn(players);

    List<Match> matches = new ArrayList<>();
    when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
      Match match = (Match) invocation.getArguments()[0];
      match.setMatchId(matches.size() + 1);
      matches.add(match);
      return match;
    });
    when(matchRepository.findAll()).thenReturn(matches);

    // When
    MatchesDto result = matchService.listDraw();

    // Then
    assertThat(result).isNotNull();
    assertThat(result.matches().size()).isEqualTo(PLAYER_COUNT * MATCH_COUNT_PER_PLAYER / 2);
    for (Player player : players) {
      assertThat(result.matches().stream()
          .filter(match -> player.getPlayerId() == match.firstPlayerId()
              || player.getPlayerId() == match.secondPlayerId())
          .count())
          .isEqualTo(MATCH_COUNT_PER_PLAYER);
    }
  }

  @Test
  public void testListDraw_invalidPlayerCount() {
    // Given
    when(playerRepository.count()).thenReturn(5L);

    // Then
    assertThrows(TournamentIllegalStateException.class, () -> {
      // When
      matchService.listDraw();
    });
  }

}
