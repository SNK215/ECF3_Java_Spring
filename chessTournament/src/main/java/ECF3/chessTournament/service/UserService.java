package ECF3.chessTournament.service;

import ECF3.chessTournament.exception.NotLoggedInException;
import ECF3.chessTournament.exception.UserExistsException;
import ECF3.chessTournament.exception.UserUnknownException;
import ECF3.chessTournament.repository.UserRepository;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    public boolean register(String firstname, String lastname, String username, String password, boolean isAdmin, List<Game> gameList) throws UserExistsException {
        if (userRepository.findByUsername(username) != null) {
            throw new UserExistsException();
        }
        else {
            User user = new User(firstname, lastname, username, password, isAdmin, gameList);
            userRepository.save(user);
            return user.getId() > 0;
        }
    }

    public boolean logIn(String username, String password) throws UserUnknownException {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return loginService.login(user);
            }
            else {
                return false;
            }
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

    public List<User> findAllOrderByScore() throws NotLoggedInException {
        if (loginService.isLogged()) {
            List<User> userList = findAll();
            Collections.sort(userList, Comparator.comparingInt(User::getScore).reversed());
            return userList;
        }
        throw new NotLoggedInException();
    }

}
