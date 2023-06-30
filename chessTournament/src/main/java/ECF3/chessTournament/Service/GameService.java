package ECF3.chessTournament.Service;

import ECF3.chessTournament.Exception.GameUnknownException;
import ECF3.chessTournament.Exception.UserExistsException;
import ECF3.chessTournament.Repository.GameRepository;
import ECF3.chessTournament.Repository.UserRepository;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    UserRepository userRepository;

    public boolean createGame(List<User> userList) {
        try {
            for (User u : userList) {
                userRepository.findByUsername(u.getUsername());
            }
            throw new UserExistsException();
        }
        catch (Exception e) {
            Game game = new Game(userList);
            gameRepository.save(game);
            return game.getId()>0;
        }
    }

    public List<Game> findMyGamesNotPlayed(boolean hasBeenPlayed, User user) {
        return gameRepository.findByHasBeenPlayedAndAndUserListContains(hasBeenPlayed, user);
    }

    public Game findById(int id) throws GameUnknownException {
        try {
            Game game = gameRepository.findById(id);
            return game;
        }
        catch (Exception e) {
            throw new GameUnknownException();
        }
    }

    public boolean updateGame(Game game) throws GameUnknownException {
        try {
            gameRepository.save(game);
            return true;
        }
        catch (Exception e) {
            throw new GameUnknownException();
        }
    }
}
