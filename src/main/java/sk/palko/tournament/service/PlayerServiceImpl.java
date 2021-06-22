package sk.palko.tournament.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.exception.MaxPlayerCountReachedException;
import sk.palko.tournament.repository.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PlayerServiceImpl implements PlayerService {

  public static final int MAX_PLAYER_COUNT = 6;

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public List<PlayerDto> listAllPlayers() {
    return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  public PlayerDto createPlayer(PlayerRequestDto playerRequest) {
    if (playerRepository.count() >= MAX_PLAYER_COUNT) {
      throw new MaxPlayerCountReachedException(MAX_PLAYER_COUNT);
    }

    Player player = mapToDomain(playerRequest);
    player = playerRepository.save(player);
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

}
