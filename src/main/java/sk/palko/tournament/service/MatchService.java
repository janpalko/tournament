package sk.palko.tournament.service;

import sk.palko.tournament.dto.MatchResultDto;
import sk.palko.tournament.dto.MatchesDto;
import sk.palko.tournament.dto.WinnersDto;

public interface MatchService {

  MatchesDto listDraw();

  MatchResultDto getMatchResult(int matchId);

  void setMatchResult(int matchId, String result);

  WinnersDto getWinners();

}
