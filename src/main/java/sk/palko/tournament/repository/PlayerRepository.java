package sk.palko.tournament.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sk.palko.tournament.domain.Player;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {

  @Query("""
      SELECT p
      FROM Player p
      WHERE p.points = (SELECT MAX(points) FROM Player)
      ORDER BY p.name ASC
      """)
  Iterable<Player> findAllWithMaxPoints();

}
