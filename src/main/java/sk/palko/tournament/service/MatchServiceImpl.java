package sk.palko.tournament.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.palko.tournament.domain.Match;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.MatchInfoDto;
import sk.palko.tournament.dto.MatchResultDto;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.WinnersDto;
import sk.palko.tournament.exception.NotFoundException;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MatchServiceImpl implements MatchService {

  public static final int NUM_OF_MATCHES_PER_PLAYER = 3;
  public static final String SCORE_SEPARATOR = ":";
  public static final String MATCH_NOT_FOUND_MESSAGE = "Match with ID '%d' was not found";

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public MatchesDto listDraw() {
    if (matchRepository.count() == 0) {
      createDraw(NUM_OF_MATCHES_PER_PLAYER);
    }

    return new MatchesDto(StreamSupport.stream(matchRepository.findAll().spliterator(), false)
        .map(this::mapToInfoDto)
        .collect(Collectors.toList())
    );
  }

  private void createDraw(int numOfMatchesPerPlayer) {
    // TODO
    Map<Integer, Player> players = StreamSupport.stream(playerRepository.findAll().spliterator(), false)
        .collect(Collectors.toMap(Player::getPlayerId, Function.identity()));

    Set<Integer> playerIds = players.keySet();

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
