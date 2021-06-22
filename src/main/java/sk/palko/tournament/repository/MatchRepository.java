package sk.palko.tournament.repository;

import org.springframework.data.repository.CrudRepository;
import sk.palko.tournament.domain.Match;

public interface MatchRepository extends CrudRepository<Match, Integer> {
}
