package sk.palko.tournament.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.dto.MessageDto;
import sk.palko.tournament.dto.PlayersDto;
import sk.palko.tournament.exception.MaxPlayerCountReachedException;
import sk.palko.tournament.service.PlayerService;
import sk.palko.tournament.service.PlayerServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

  @Autowired
  private PlayerService playerService;

  @GetMapping
  public PlayersDto getPlayers() {
    return new PlayersDto(playerService.listAllPlayers());
  }

  @PostMapping
  public ResponseEntity<PlayerDto> createPlayer(@RequestBody @Valid PlayerRequestDto playerRequest) {
    PlayerDto player = playerService.createPlayer(playerRequest);
    return new ResponseEntity<>(player, HttpStatus.OK); // should be HttpStatus.CREATED 201
  }

}
