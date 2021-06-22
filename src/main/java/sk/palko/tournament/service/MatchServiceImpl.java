package sk.palko.tournament.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.MatchInfoDto;
import sk.palko.tournament.dto.MatchResultDto;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.WinnersDto;
import sk.palko.tournament.exception.InsufficientPlayerCountException;
import sk.palko.tournament.exception.NoFreePlayerFoundException;
import sk.palko.tournament.exception.NotFoundException;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static sk.palko.tournament.service.PlayerServiceImpl.PLAYER_COUNT;

@Service
public class MatchServiceImpl implements MatchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);

  public static final int MATCH_COUNT_PER_PLAYER = 3;
  public static final int IMPOSSIBLE_MATCH = 0;
  public static final int CREATE_MATCH = 1;
  public static final int CREATED_MATCH = 2;
  public static final String SCORE_SEPARATOR = ":";
  public static final String MATCH_NOT_FOUND_MESSAGE = "Match with ID '%d' was not found";

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public MatchesDto listDraw() {
    if (matchRepository.count() == 0) {
      createDraw();
    }
    return new MatchesDto(StreamSupport.stream(matchRepository.findAll().spliterator(), false)
        .map(this::mapToInfoDto)
        .collect(Collectors.toList())
    );
  }

  protected void createDraw() {
    long currentPlayerCount = playerRepository.count();
    if (playerRepository.count() != PLAYER_COUNT) {
      throw new InsufficientPlayerCountException(PLAYER_COUNT, currentPlayerCount);
    }

    Player[] players = initPlayers();
    Integer[][] matches = initMatches();
    IntStream.range(0, PLAYER_COUNT).forEach(playerIndex -> createMatchesForPlayer(players, matches, playerIndex));
    logDraw(matches);
  }

  private Player[] initPlayers() {
    Player[] players = new Player[PLAYER_COUNT];
    int i = 0;
    for (Player player : playerRepository.findAll()) {
      players[i++] = player;
    }
    return players;
  }

  private Integer[][] initMatches() {
    Integer[][] matches = new Integer[PLAYER_COUNT][PLAYER_COUNT];
    IntStream.range(0, PLAYER_COUNT).forEach(i -> matches[i][i] = IMPOSSIBLE_MATCH);
    return matches;
  }

  private void createMatchesForPlayer(Player[] players, Integer[][] matches, int playerIndex) {
    for (long matchCount = getMatchCount(matches[playerIndex]); matchCount < MATCH_COUNT_PER_PLAYER; matchCount++) {
      Set<Integer> possibleOpponents = getOpponentsWithMinMatches(matches, playerIndex);
      int randomOpponentIndex = possibleOpponents.stream()
          .skip(new Random().nextInt(possibleOpponents.size()))
          .findFirst()
          .orElseThrow(NoFreePlayerFoundException::new); // should not happen

      createMatch(players[playerIndex], players[randomOpponentIndex]);
      matches[playerIndex][randomOpponentIndex] = CREATE_MATCH;
      matches[randomOpponentIndex][playerIndex] = CREATED_MATCH;
    }
  }

  private long getMatchCount(Integer[] matches) {
    return Arrays.stream(matches).filter(j -> j != null && j != IMPOSSIBLE_MATCH).count();
  }

  private Set<Integer> getOpponentsWithMinMatches(Integer[][] matches, int playerIndex) {
    // key = num of created matches, value = list of possible opponents
    Map<Long, Set<Integer>> matchCounts = new LinkedHashMap<>(MATCH_COUNT_PER_PLAYER);
    for (long i = 0; i < MATCH_COUNT_PER_PLAYER; i++) {
      matchCounts.put(i, new HashSet<>());
    }

    // Assign opponents to groups based on already created matches
    for (int i = 0; i < PLAYER_COUNT; i++) {
      if (i == playerIndex) {
        continue;
      }
      long matchCount = getMatchCount(matches[i]);
      if (matchCount < MATCH_COUNT_PER_PLAYER) {
        matchCounts.get(matchCount).add(i);
      }
    }

    // Find first non empty group of possible opponents with minimum created matches
    for (Set<Integer> possibleOpponents : matchCounts.values()) {
      if (!possibleOpponents.isEmpty()) {
        return possibleOpponents;
      }
    }

    throw new NoFreePlayerFoundException(); // should not happen
  }

  private void createMatch(Player firstPlayer, Player secondPlayer) {
    Match match = new Match();
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    matchRepository.save(match);
  }

  private void logDraw(Integer[][] matches) {
    LOGGER.info("Created draw (0 = impossible match, 1 = match to be created, 2 = match created by opponent)");
    LOGGER.info("  | " + IntStream.range(0, PLAYER_COUNT).mapToObj(i -> (i + 1) + "").collect(Collectors.joining(" "))); // Column header
    LOGGER.info(IntStream.range(0, 3 + PLAYER_COUNT * 2).mapToObj(i -> "-").collect(Collectors.joining()));
    for (int i = 0; i < PLAYER_COUNT; i++) {
      StringBuilder sb = new StringBuilder();
      sb.append((i + 1) + " |"); // Row header
      for (int j = 0; j < PLAYER_COUNT; j++) {
        Optional<Integer> value = Optional.ofNullable(matches[i][j]);
        sb.append(" " + (value.isPresent() ? value.get() : "-"));
      }
      LOGGER.info(sb.toString());
    }
  }

  private MatchInfoDto mapToInfoDto(Match match) {
    return new MatchInfoDto(match.getMatchId(), match.getFirstPlayer().getPlayerId(), match.getSecondPlayer().getPlayerId());
  }

  @Override
  public MatchResultDto getMatchResult(int matchId) {
    Optional<Match> match = matchRepository.findById(matchId);
    return match.map(this::mapToResultDto)
        .orElseThrow(() -> new NotFoundException(String.format(MATCH_NOT_FOUND_MESSAGE, matchId)));
  }

  private MatchResultDto mapToResultDto(Match match) {
    String result = null;
    if (match.getFirstPlayerScore() != null && match.getSecondPlayerScore() != null) {
      result = match.getFirstPlayerScore() + SCORE_SEPARATOR + match.getSecondPlayerScore();
    }
    return new MatchResultDto(match.getFirstPlayer().getName(), match.getSecondPlayer().getName(), result);
  }

  @Override
  public void setMatchResult(int matchId, String result) {
    Optional<Match> match = matchRepository.findById(matchId);
    if (match.isPresent()) {
      int[] score = parseResult(result);
      Match matchValue = match.get();
      matchValue.setFirstPlayerScore(score[0]);
      matchValue.setSecondPlayerScore(score[1]);
      matchRepository.save(matchValue);
    } else {
      throw new NotFoundException(String.format(MATCH_NOT_FOUND_MESSAGE, matchId));
    }
  }

  private int[] parseResult(String result) {
    String[] score = result.split(SCORE_SEPARATOR);
    return new int[] {Integer.parseInt(score[0]), Integer.parseInt(score[1])};
  }

  @Override
  public WinnersDto getWinners() {
    // TODO
    PlayerDto player1 = new PlayerDto("Player 1", 1);
    PlayerDto player2 = new PlayerDto("Player 2", 2);
    return new WinnersDto(player1, player2);
  }

}
