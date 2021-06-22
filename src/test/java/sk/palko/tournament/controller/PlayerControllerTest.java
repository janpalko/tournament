package sk.palko.tournament.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import sk.palko.tournament.dto.ErrorMessageDto;
import sk.palko.tournament.dto.PlayerDto;
import sk.palko.tournament.dto.PlayerRequestDto;
import sk.palko.tournament.dto.PlayersDto;
import sk.palko.tournament.repository.MatchRepository;
import sk.palko.tournament.repository.PlayerRepository;
import sk.palko.tournament.service.PlayerService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlayerService playerService;
  @MockBean
  private PlayerRepository playerRepository;
  @MockBean
  private MatchRepository matchRepository;

  @Test
  public void testCreatePlayer() throws Exception {
    // Given
    PlayerRequestDto request = new PlayerRequestDto("Name 1", 18);
    when(playerService.createPlayer(any(PlayerRequestDto.class))).thenReturn(new PlayerDto("Name 1", 18));

    // When
    MockHttpServletResponse response = mockMvc.perform(post("/players")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andReturn()
        .getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);

    PlayerDto result = objectMapper.readValue(response.getContentAsByteArray(), PlayerDto.class);
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo(request.name());
  }

  @Test
  public void testCreatePlayer_invalidEmptyNameAndLowAge() throws Exception {
    // Given
    PlayerRequestDto request = new PlayerRequestDto("", 1);

    // When
    MockHttpServletResponse response = mockMvc.perform(post("/players")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andReturn()
        .getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

    ErrorMessageDto result = objectMapper.readValue(response.getContentAsByteArray(), ErrorMessageDto.class);
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(result.error()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.messages()).isNotEmpty();
    assertThat(result.messages().size()).isEqualTo(2);
    assertThat(result.messages()).containsExactlyInAnyOrder("Name should not be blank", "Age should not be less than 18");
  }

  @Test
  public void testCreatePlayer_invalidHighAge() throws Exception {
    // Given
    PlayerRequestDto request = new PlayerRequestDto("Name 1", 150);

    // When
    MockHttpServletResponse response = mockMvc.perform(post("/players")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
        .andReturn()
        .getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

    ErrorMessageDto result = objectMapper.readValue(response.getContentAsByteArray(), ErrorMessageDto.class);
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(result.error()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.messages()).isNotEmpty();
    assertThat(result.messages().size()).isEqualTo(1);
    assertThat(result.messages()).containsExactly("Age should not be greater than 130");
  }

  @Test
  public void testGetPlayers() throws Exception {
    // Given
    PlayerDto player = new PlayerDto("Name 1", 1);
    when(playerService.listAllPlayers()).thenReturn(Arrays.asList(player));

    // When
    MockHttpServletResponse response = mockMvc.perform(get("/players")).andReturn().getResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

    PlayersDto result = objectMapper.readValue(response.getContentAsByteArray(), PlayersDto.class);
    assertThat(result).isNotNull();
    assertThat(result.players()).isNotEmpty();
    assertThat(result.players().size()).isEqualTo(1);
    assertThat(result.players().get(0).name()).isEqualTo(player.name());
  }

}
