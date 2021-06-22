package sk.palko.tournament.service;

import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;

import java.util.List;

public interface PlayerService {

  List<PlayerDto> listAllPlayers();

  PlayerDto createPlayer(PlayerRequestDto playerRequest);

}
