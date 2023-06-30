package ECF3.chessTournament.service;

import ECF3.chessTournament.exception.GameUnknownException;
import ECF3.chessTournament.exception.UserExistsException;
import ECF3.chessTournament.exception.UserUnknownException;
import ECF3.chessTournament.repository.GameRepository;
import ECF3.chessTournament.repository.UserRepository;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    UserRepository userRepository;

    public boolean createGame(List<User> userList) throws UserUnknownException {
        try {
            for (User u : userList) {
                userRepository.findByUsername(u.getUsername());
            }
            Game game = new Game(userList);
            gameRepository.save(game);
            return game.getId()>0;
        }
        catch (Exception e) {
            throw new UserUnknownException();
        }
    }

    public List<Game> findMyGames(boolean hasBeenPlayed, User user) throws GameUnknownException {
        try {
            return gameRepository.findByHasBeenPlayedAndAndUserListContains(hasBeenPlayed, user);
        }
        catch (Exception e) {
            throw new GameUnknownException();
        }
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
