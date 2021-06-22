package sk.palko.tournament.dto;

import java.util.Arrays;
import java.util.List;

public record WinnersDto(List<PlayerDto> winners) {

  public WinnersDto(PlayerDto... winners) {
    this(Arrays.stream(winners).toList());
  }

}
