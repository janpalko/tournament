package sk.palko.tournament.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.dto.WinnersDto;
import sk.palko.tournament.exception.MaxPlayerCountReachedException;
import sk.palko.tournament.exception.TournamentIllegalStateException;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceImpl.class);

  public static final int PLAYER_COUNT = 6;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private MatchRepository matchRepository;

  @Override
  public List<PlayerDto> listAllPlayers() {
    return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  public PlayerDto createPlayer(PlayerRequestDto playerRequest) {
    if (playerRepository.count() >= PLAYER_COUNT) {
      throw new MaxPlayerCountReachedException(PLAYER_COUNT);
    }

    Player player = mapToDomain(playerRequest);
    player = playerRepository.save(player);
    LOGGER.debug("Created " + player);
    return mapToDto(player);
  }

  private Player mapToDomain(PlayerRequestDto playerRequest) {
    Player player = new Player();
    player.setName(playerRequest.name());
    player.setAge(playerRequest.age());
    return player;
  }

  private PlayerDto mapToDto(Player player) {
    return new PlayerDto(player.getName(), player.getPlayerId());
  }

  @Override
  public WinnersDto getWinners() {
    if (matchRepository.count() == 0) {
      throw new TournamentIllegalStateException("Unable to determine winner due to missing matches");
    }

    long incompleteMatchCount = matchRepository.countByResultIsNull();
    if (incompleteMatchCount > 0) {
      throw new TournamentIllegalStateException("Unable to determine winner because there are some incomplete matches");
    }

    List<PlayerDto> players = StreamSupport.stream(playerRepository.findAllWithMaxPoints().spliterator(), false)
        .map(this::mapToDto)
        .collect(Collectors.toList());

    return new WinnersDto(players);
  }

}
