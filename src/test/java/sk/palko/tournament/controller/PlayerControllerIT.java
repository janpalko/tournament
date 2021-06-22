package sk.palko.tournament.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.dto.PlayersDto;
import sk.palko.tournament.repository.PlayerRepository;
import sk.palko.tournament.util.PlayerData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PlayerRepository playerRepository;

  @BeforeEach
  public void init() {
    playerRepository.deleteAll();
  }

  @Test
  public void testCreatePlayer() throws Exception {
    // Given
    PlayerRequestDto request = new PlayerRequestDto("Name 1", 18);

    // When
    MockHttpServletResponse response = mockMvc.perform(post("/players")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andReturn()
        .getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

    PlayerDto result = objectMapper.readValue(response.getContentAsByteArray(), PlayerDto.class);
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo(request.name());
  }

  @Test
  public void testGetPlayers() throws Exception {
    // Given
    Player player = PlayerData.createPlayer(2);
    playerRepository.save(player);

    // When
    MockHttpServletResponse response = mockMvc.perform(get("/players")).andReturn().getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

    PlayersDto result = objectMapper.readValue(response.getContentAsByteArray(), PlayersDto.class);
    assertThat(result).isNotNull();
    assertThat(result.players()).isNotEmpty();
    assertThat(result.players().size()).isEqualTo(1);
    assertThat(result.players().get(0).name()).isEqualTo("Name 2");
  }

}
