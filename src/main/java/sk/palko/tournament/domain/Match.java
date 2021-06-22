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
  private Integer firstPlayerScore;

  @Column
  private Integer secondPlayerScore;

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

  public Integer getFirstPlayerScore() {
    return firstPlayerScore;
  }

  public void setFirstPlayerScore(Integer firstPlayerScore) {
    this.firstPlayerScore = firstPlayerScore;
  }

  public Integer getSecondPlayerScore() {
    return secondPlayerScore;
  }

  public void setSecondPlayerScore(Integer secondPlayerScore) {
    this.secondPlayerScore = secondPlayerScore;
  }

  @Override
  public int hashCode() {
    //return Objects.hashCode(matchId);
    return Objects.hash(firstPlayer.getPlayerId(), secondPlayer.getPlayerId());
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
    //return matchId.equals(match.matchId);
    return Objects.equals(firstPlayer.getPlayerId(), match.getFirstPlayer().getPlayerId())
        && Objects.equals(secondPlayer.getPlayerId(), match.getSecondPlayer().getPlayerId());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Match.class.getSimpleName() + "[", "]")
        .add("matchId=" + matchId)
        .add("firstPlayer=" + firstPlayer)
        .add("secondPlayer=" + secondPlayer)
        .add("firstPlayerScore=" + firstPlayerScore)
        .add("secondPlayerScore=" + secondPlayerScore)
        .toString();
  }

}
