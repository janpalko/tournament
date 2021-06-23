package sk.palko.tournament.service;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.MatchResultDto;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.exception.NotFoundException;
import sk.palko.tournament.exception.TournamentIllegalStateException;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;
import sk.palko.tournament.util.MatchData;
import sk.palko.tournament.util.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

  @RepeatedTest(50)
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

  @Test
  public void testGetMatchResult() {
    // Given
    Player firstPlayer = PlayerData.createPlayerWithId(1);
    Player secondPlayer = PlayerData.createPlayerWithId(2);
    Match match = MatchData.createMatchWithId(1, firstPlayer, secondPlayer);
    when(matchRepository.findById(1)).thenReturn(Optional.of(match));

    // When
    MatchResultDto result = matchService.getMatchResult(1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.firstPlayer()).isEqualTo(firstPlayer.getName());
    assertThat(result.secondPlayer()).isEqualTo(secondPlayer.getName());
    assertThat(result.result()).isEqualTo(match.getResult());
  }

  @Test
  public void testGetMatchResult_nonExistingMatch() {
    // Then
    assertThrows(NotFoundException.class, () -> {
      // When
      matchService.getMatchResult(1);
    });
  }

  @Test
  public void testSetMatchResult() {
    // Given
    Player firstPlayer = PlayerData.createPlayerWithId(1);
    firstPlayer.setPoints(0);
    Player secondPlayer = PlayerData.createPlayerWithId(2);
    secondPlayer.setPoints(0);
    Match match = MatchData.createMatchWithId(1, firstPlayer, secondPlayer);
    when(matchRepository.findById(1)).thenReturn(Optional.of(match));

    // When
    matchService.setMatchResult(1, "1:2");

    // Then
    ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
    verify(matchRepository).save(matchCaptor.capture());
    assertThat(matchCaptor.getValue().getResult()).isEqualTo("1:2");

    ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
    verify(playerRepository, times(2)).save(playerCaptor.capture());
    List<Player> capturedPlayers = playerCaptor.getAllValues();
    assertThat(capturedPlayers.get(0).getPoints()).isEqualTo(1);
    assertThat(capturedPlayers.get(1).getPoints()).isEqualTo(3);
  }

  @Test
  public void testSetMatchResult_withPreviousResultSet() {

    // Given
    Player firstPlayer = PlayerData.createPlayerWithId(1);
    firstPlayer.setPoints(13);
    Player secondPlayer = PlayerData.createPlayerWithId(2);
    secondPlayer.setPoints(11);
    Match match = MatchData.createMatchWithId(1, firstPlayer, secondPlayer);
    match.setResult("2:0");
    when(matchRepository.findById(1)).thenReturn(Optional.of(match));

    // When
    matchService.setMatchResult(1, "1:2");

    // Then
    ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
    verify(matchRepository).save(matchCaptor.capture());
    assertThat(matchCaptor.getValue().getResult()).isEqualTo("1:2");

    ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
    verify(playerRepository, times(2)).save(playerCaptor.capture());
    List<Player> capturedPlayers = playerCaptor.getAllValues();
    assertThat(capturedPlayers.get(0).getPoints()).isEqualTo(11);
    assertThat(capturedPlayers.get(1).getPoints()).isEqualTo(13);
  }

  @Test
  public void testSetMatchResult_nonExistingMatch() {
    // Then
    assertThrows(NotFoundException.class, () -> {
      // When
      matchService.setMatchResult(1, "1:2");
    });
  }

}
