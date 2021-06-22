package sk.palko.tournament.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sk.palko.tournament.domain.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {

  long countByResultIsNull();

}
