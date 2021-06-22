package sk.palko.tournament.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.palko.tournament.dto.MatchResultDto;
import sk.palko.tournament.dto.MatchResultRequestDto;
import sk.palko.tournament.dto.MessageDto;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.dto.WinnersDto;
import sk.palko.tournament.service.MatchService;
import sk.palko.tournament.service.PlayerService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/tournaments")
@Validated
public class TournamentController {

  public static final String MATCH_ID_VALIDATION_MESSAGE = "Match ID should be greater than 0";

  @Autowired
  private PlayerService playerService;

  @Autowired
  private MatchService matchService;

  @GetMapping("/draw")
  public MatchesDto getDraw() {
    return matchService.listDraw();
  }

  @GetMapping("/results/{matchId}")
  public MatchResultDto getMatchResult(@PathVariable @Positive(message = MATCH_ID_VALIDATION_MESSAGE) int matchId) {
    return matchService.getMatchResult(matchId);
  }

  @PutMapping("/results/{matchId}")
  public ResponseEntity<MessageDto> setMatchResult(@PathVariable @Positive(message = MATCH_ID_VALIDATION_MESSAGE) int matchId,
                                                   @RequestBody /*@Valid*/ MatchResultRequestDto matchResultRequest) {
    String result = matchResultRequest.result();
    switch (result) {
      case "2:0":
      case "2:1":
      case "0:2":
      case "1:2":
        matchService.setMatchResult(matchId, matchResultRequest.result());
        return new ResponseEntity<>(new MessageDto("Score registered"), HttpStatus.OK);
      default:
        return new ResponseEntity<>(new MessageDto("Invalid score provided"), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/winner")
  public WinnersDto getWinners() {
    return playerService.getWinners();
  }

}
