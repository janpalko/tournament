package sk.palko.tournament.dto;

import java.util.Arrays;
import java.util.List;

public record MatchesDto(List<MatchInfoDto> matches) {

  public MatchesDto(MatchInfoDto... matches) {
    this(Arrays.stream(matches).toList());
  }

}
