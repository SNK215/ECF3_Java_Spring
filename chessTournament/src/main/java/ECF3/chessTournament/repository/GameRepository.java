package ECF3.chessTournament.repository;

import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    public List<Game> findByHasBeenPlayed(boolean hasBeenPlayed);

    public List<Game> findByHasBeenPlayedAndAndUserListContains(boolean hasBeenPlayed, User user);

    public Game findById(int id);
}
