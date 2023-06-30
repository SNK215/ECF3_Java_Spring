package ECF3.chessTournament.Service;

import ECF3.chessTournament.Exception.NotLoggedInException;
import ECF3.chessTournament.Exception.UserExistsException;
import ECF3.chessTournament.Exception.UserUnknownException;
import ECF3.chessTournament.Repository.UserRepository;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    public boolean register(String firstname, String lastname, String username, String password, boolean isAdmin, List<Game> gameList) throws UserExistsException {
        try {
            userRepository.findByUsername(username);
            throw new UserExistsException();
        } catch (Exception e) {
            User user = new User(firstname, lastname, username, password, isAdmin, gameList);
            userRepository.save(user);
            return user.getId() > 0;
        }
    }

    public boolean logIn(String username, String password) throws UserUnknownException {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password);
            return loginService.login(user);
        } catch (Exception e) {
            throw new UserUnknownException();
        }
    }

    public boolean logOut() {
        return loginService.logout();
    }

    public User findById(int id) throws UserUnknownException {
        try {
            User user = userRepository.findById(id);
            return user;
        } catch (Exception e) {
            throw new UserUnknownException();
        }
    }

    public User findByUsername(String username) throws UserUnknownException {
        try {
            User user = userRepository.findByUsername(username);
            return user;
        }
        catch (Exception e) {
            throw new UserUnknownException();
        }
    }

    public List<User> findAll() throws NotLoggedInException {
        if(loginService.isLogged()) {
            return (List<User>) userRepository.findAll();
        }
        throw new NotLoggedInException();
    }

    public List<User> findAllOrderdByScore() throws NotLoggedInException {
        List<User> userList = findAll();
        Collections.sort(userList, Comparator.comparingInt(User::getScore).reversed());

        return userList;
    }

}
