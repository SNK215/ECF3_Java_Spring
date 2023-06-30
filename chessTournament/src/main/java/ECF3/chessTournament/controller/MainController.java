package ECF3.chessTournament.controller;

import ECF3.chessTournament.Exception.GameUnknownException;
import ECF3.chessTournament.Exception.NotLoggedInException;
import ECF3.chessTournament.Exception.UserExistsException;
import ECF3.chessTournament.Exception.UserUnknownException;
import ECF3.chessTournament.Service.GameService;
import ECF3.chessTournament.Service.UserService;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/home")
public class MainController {
    @Autowired
    UserService userService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    GameService gameService;

    @Autowired
    HttpSession httpSession;

    public void displayHeaderButtonsCorrectly(ModelAndView modelAndView){
        if (httpSession.getAttribute("isLogged") != null) {
            String isLogged = httpSession.getAttribute("isLogged").toString();
            modelAndView.addObject("isLogged",isLogged);
            String isAdmin = httpSession.getAttribute("isAdmin").toString();
            modelAndView.addObject("isAdmin", isAdmin);
        } else {
            modelAndView.addObject("isLogged","false");
            modelAndView.addObject("isAdmin","false");
        }
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("homepage");
        displayHeaderButtonsCorrectly(modelAndView);

        return modelAndView;
    }

    @GetMapping("/registerForm")
    public ModelAndView registerForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerForm");
        displayHeaderButtonsCorrectly(modelAndView);

        return modelAndView;
    }

    @PostMapping("/registerAction")
    public String registerAction(@RequestParam String firstname, @RequestParam String lastname, @RequestParam String username, @RequestParam String password) throws UserExistsException {
        List<Game> gameList = new ArrayList<>();
        if (userService.register(firstname, lastname, username, password, false, gameList)) {
            return "redirect:/home/loginForm";
        }
        return null;
    }

    @GetMapping("/loginForm")
    public ModelAndView loginForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginForm");
        displayHeaderButtonsCorrectly(modelAndView);

        return modelAndView;
    }

    @PostMapping("/loginAction")
    public String loginAction(@RequestParam String username, @RequestParam String password) throws UserUnknownException {
        if (userService.logIn(username, password)) {
            return "redirect:/home/profilePage";
        }
        return null;
    }

    @GetMapping("/logoutAction")
    public String logoutAction() {
        if (userService.logOut()) {
            return "redirect:/home/";
        }
        return null;
    }

    @GetMapping("/profilePage")
    public ModelAndView profilePage() throws UserUnknownException, NotLoggedInException {
        int userId = (int)httpSession.getAttribute("userId");
        User user = userService.findById(userId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profilePage");
        modelAndView.addObject("user", user);
        displayHeaderButtonsCorrectly(modelAndView);

        List<User> userList = userService.findAllOrderdByScore();
        int userRanking = userList.indexOf(user) + 1;
        modelAndView.addObject("userRanking", userRanking);

        List<Game> gameList = gameService.findMyGamesNotPlayed(true, user);
        modelAndView.addObject("gameList", gameList);

        if (httpSession.getAttribute("isLogged").toString().equals("false")) {
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @GetMapping("/gameManagement")
    public ModelAndView gameManagement() throws NotLoggedInException, UserUnknownException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("gameManagement");
        displayHeaderButtonsCorrectly(modelAndView);

        List<User> userList = userService.findAll();
        modelAndView.addObject("userList", userList);

        int userId = (int) httpSession.getAttribute("userId");
        User user = userService.findById(userId);

        List<Game> gamesNotPlayed = gameService.findMyGamesNotPlayed(false, user);
        modelAndView.addObject("gameList", gamesNotPlayed);

        return modelAndView;
    }

    @PostMapping("/gameCreation")
    public String gameCreation(@RequestParam String userAString, @RequestParam String userBString) throws UserUnknownException {
        User userA = userService.findByUsername(userAString);
        User userB = userService.findByUsername(userBString);
        List<User> userList = new ArrayList<>();
        userList.add(userA);
        userList.add(userB);
        if (gameService.createGame(userList)) {
            return "redirect:/home/gameManagement";
        }
        return null;
    }

    @PostMapping("/setResult")
    public String setResult(@RequestParam String result, @RequestParam int gameId) throws GameUnknownException {
        Game game = gameService.findById(gameId);
        game.setHasBeenPlayed(true);
        game.setResult(result);

        if (game.getResult().equals("A")) {
            game.getUserList().get(0).setScore(game.getUserList().get(0).getScore()+2);
        } else if (game.getResult().equals("B")) {
            game.getUserList().get(1).setScore(game.getUserList().get(1).getScore()+2);
        } else if (game.getResult().equals("N")) {
            game.getUserList().get(0).setScore(game.getUserList().get(0).getScore()+1);
            game.getUserList().get(1).setScore(game.getUserList().get(1).getScore()+1);
        }
        gameService.updateGame(game);
        return "redirect:/home/profilePage";
    }

    @GetMapping("/rankings")
    public ModelAndView rankings() throws NotLoggedInException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rankings");
        displayHeaderButtonsCorrectly(modelAndView);
        List<User> userList = userService.findAllOrderdByScore();
        modelAndView.addObject("userList", userList);

        return modelAndView;
    }






}
