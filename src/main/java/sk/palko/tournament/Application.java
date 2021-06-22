package sk.palko.tournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import sk.palko.tournament.domain.Player;
import sk.palko.tournament.repository.PlayerRepository;

@EnableJpaRepositories(basePackageClasses = PlayerRepository.class)
@EntityScan(basePackageClasses = Player.class)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
