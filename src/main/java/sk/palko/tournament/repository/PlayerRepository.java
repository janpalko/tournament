package sk.palko.tournament.repository;

import org.springframework.data.repository.CrudRepository;
import sk.palko.tournament.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}
