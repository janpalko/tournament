package sk.palko.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"firstPlayerId", "secondPlayerId"}))
public class Match {

  public static final String SCORE_SEPARATOR = ":";

  @Id
  @GeneratedValue
  private Integer matchId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "firstPlayerId", nullable = false)
  private Player firstPlayer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "secondPlayerId", nullable = false)
  private Player secondPlayer;

  @Column
  private String result;

  public Integer getMatchId() {
    return matchId;
  }

  public void setMatchId(Integer matchId) {
    this.matchId = matchId;
  }

  public Player getFirstPlayer() {
    return firstPlayer;
  }

  public void setFirstPlayer(Player firstPlayer) {
    this.firstPlayer = firstPlayer;
  }

  public Player getSecondPlayer() {
    return secondPlayer;
  }

  public void setSecondPlayer(Player secondPlayer) {
    this.secondPlayer = secondPlayer;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public int hashCode() {
    //return Objects.hashCode(getMatchId());
    return Objects.hash(getFirstPlayer(), getSecondPlayer());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Match match = (Match) o;
    //return getMatchId().equals(match.getMatchId());
    return Objects.equals(getFirstPlayer(), match.getFirstPlayer())
        && Objects.equals(getSecondPlayer(), match.getSecondPlayer());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Match.class.getSimpleName() + "[", "]")
        .add("matchId=" + matchId)
        .add("firstPlayer=" + firstPlayer)
        .add("secondPlayer=" + secondPlayer)
        .add("result='" + result + "'")
        .toString();
  }

}
