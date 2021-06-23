package sk.palko.tournament.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.dto.WinnersDto;
import sk.palko.tournament.exception.MaxPlayerCountReachedException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sk.palko.tournament.service.MatchServiceImpl.MATCH_COUNT_PER_PLAYER;
import static sk.palko.tournament.service.PlayerServiceImpl.PLAYER_COUNT;

@ExtendWith(SpringExtension.class)
@Import(PlayerServiceImpl.class)
public class PlayerServiceImplTest {

  @Autowired
  private PlayerService playerService;

  @MockBean
  private PlayerRepository playerRepository;
  @MockBean
  private MatchRepository matchRepository;

  @Test
  public void testListAllPlayers() {
    // Given
    List<Player> players = IntStream.rangeClosed(1, PLAYER_COUNT)
        .mapToObj(PlayerData::createPlayerWithId)
        .collect(Collectors.toList());
    when(playerRepository.findAll()).thenReturn(players);

    // When
    List<PlayerDto> result = playerService.listAllPlayers();

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.stream().map(PlayerDto::playerId).collect(Collectors.toList()))
        .containsAll(players.stream().map(Player::getPlayerId).collect(Collectors.toList()));
    assertThat(result.stream().map(PlayerDto::name).collect(Collectors.toList()))
        .containsAll(players.stream().map(Player::getName).collect(Collectors.toList()));
  }

  @Test
  public void testListAllPlayers_empty() {
    // Given
    when(playerRepository.findAll()).thenReturn(new ArrayList<>());

    // When
    List<PlayerDto> result = playerService.listAllPlayers();

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  public void testCreatePlayer() {
    // Given
    PlayerRequestDto playerRequest = new PlayerRequestDto("Name 2", 20);
    when(playerRepository.count()).thenReturn(1L);
    when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
      Player player = (Player) invocation.getArguments()[0];
      player.setPlayerId(2);
      return player;
    });

    // When
    PlayerDto result = playerService.createPlayer(playerRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo(playerRequest.name());
  }

  @Test
  public void testCreatePlayer_maxCountReached() {
    // Given
    PlayerRequestDto playerRequest = new PlayerRequestDto("Name 7", 70);
    when(playerRepository.count()).thenReturn(Long.valueOf(PLAYER_COUNT));

    // Then
    assertThrows(MaxPlayerCountReachedException.class, () -> {
      // When
      playerService.createPlayer(playerRequest);
    });
  }

  @Test
  public void testGetWinners() {
    // Given
    when(matchRepository.count()).thenReturn(PLAYER_COUNT * MATCH_COUNT_PER_PLAYER / 2L);
    when(matchRepository.countByResultIsNull()).thenReturn(0L);
    List<Player> players = IntStream.rangeClosed(1, 2)
        .mapToObj(PlayerData::createPlayerWithId)
        .collect(Collectors.toList());
    when(playerRepository.findAllWithMaxPoints()).thenReturn(players);

    // When
    WinnersDto result = playerService.getWinners();

    // Then
    assertThat(result).isNotNull();
    assertThat(result.winners()).isNotEmpty();
    assertThat(result.winners().stream().map(PlayerDto::playerId).collect(Collectors.toList()))
        .containsAll(players.stream().map(Player::getPlayerId).collect(Collectors.toList()));
    assertThat(result.winners().stream().map(PlayerDto::name).collect(Collectors.toList()))
        .containsAll(players.stream().map(Player::getName).collect(Collectors.toList()));
  }

  @Test
  public void testGetWinners_noMatches() {
    // Given
    when(matchRepository.count()).thenReturn(0L);

    // Then
    assertThrows(TournamentIllegalStateException.class, () -> {
      // When
      playerService.getWinners();
    });
  }

  @Test
  public void testGetWinners_incompleteMatches() {
    // Given
    when(matchRepository.count()).thenReturn(PLAYER_COUNT * MATCH_COUNT_PER_PLAYER / 2L);
    when(matchRepository.countByResultIsNull()).thenReturn(1L);

    // Then
    assertThrows(TournamentIllegalStateException.class, () -> {
      // When
      playerService.getWinners();
    });
  }

}
