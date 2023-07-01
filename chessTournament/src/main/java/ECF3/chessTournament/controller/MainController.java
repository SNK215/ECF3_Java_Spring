package ECF3.chessTournament.controller;

import ECF3.chessTournament.exception.*;
import ECF3.chessTournament.service.GameService;
import ECF3.chessTournament.service.LoginService;
import ECF3.chessTournament.service.UserService;
import ECF3.chessTournament.entity.Game;
import ECF3.chessTournament.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    LoginService loginService;

    @Autowired
    GameService gameService;

    @Autowired
    HttpSession httpSession;

    public void getLoggedStatusForHeaderButtons(ModelAndView modelAndView){
        if (httpSession.getAttribute("isLogged") != null ) {
            String isLogged = httpSession.getAttribute("isLogged").toString();
            modelAndView.addObject("isLogged",isLogged);
        } else {
            modelAndView.addObject("isLogged","false");
        }
    }

    @GetMapping("/")
    public ModelAndView home() throws NotLoggedInException, UserUnknownException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("homepage");
        getLoggedStatusForHeaderButtons(modelAndView);

        return modelAndView;
    }

    @GetMapping("/registerForm")
    public ModelAndView registerForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerForm");
        getLoggedStatusForHeaderButtons(modelAndView);

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
        getLoggedStatusForHeaderButtons(modelAndView);

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
    public ModelAndView profilePage() throws UserUnknownException, NotLoggedInException, GameUnknownException {
        ModelAndView modelAndView = new ModelAndView();
        getLoggedStatusForHeaderButtons(modelAndView);

        if (!loginService.isLogged()) {
            modelAndView.setViewName("loginForm");
        }
        else {
            int userId = (int)httpSession.getAttribute("userId");
            User user = userService.findById(userId);
            modelAndView.addObject("user", user);

            List<User> userList = userService.findAllOrderByScore();
            int userRanking = userList.indexOf(user) + 1;
            modelAndView.addObject("userRanking", userRanking);

            List<Game> gameList = gameService.findMyGames(true, user);
            modelAndView.addObject("gameList", gameList);
            modelAndView.setViewName("profilePage");
        }
        return modelAndView;
    }

    @GetMapping("/gameManagement")
    public ModelAndView gameManagement() throws NotLoggedInException, UserUnknownException, GameUnknownException {
        ModelAndView modelAndView = new ModelAndView();
        getLoggedStatusForHeaderButtons(modelAndView);

        if (!loginService.isLogged()) {
            modelAndView.setViewName("loginForm");
        }
        else {
            List<User> userList = userService.findAll();
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).isAdmin()) {
                    userList.remove(i);
                }
            }
            modelAndView.addObject("userList", userList);

            int userId = (int) httpSession.getAttribute("userId");
            User user = userService.findById(userId);

            List<Game> gamesNotPlayed = gameService.findMyGames(false, user);

            modelAndView.addObject("isAdmin",user.isAdmin());
            modelAndView.addObject("gameList", gamesNotPlayed);

            modelAndView.setViewName("gameManagement");
        }

        return modelAndView;
    }


    @PostMapping("/resetTournament")
    public String resetTournament() throws Exception {
        if (userService.resetScores()) {
            return "redirect:/home/rankings";
        }
        return null;
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
        getLoggedStatusForHeaderButtons(modelAndView);

        if (!loginService.isLogged()) {
            modelAndView.setViewName("loginForm");
        }
        else {
            List<User> userList = userService.findAllOrderByScore();
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).isAdmin()) {
                    userList.remove(i);
                }
            }
            modelAndView.addObject("userList", userList);
            modelAndView.setViewName("rankings");
        }

        return modelAndView;
    }

    @ExceptionHandler(UserExistsException.class)
    public ModelAndView handleUserExist(UserExistsException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(UserUnknownException.class)
    public ModelAndView handleUserExist(UserUnknownException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(NotLoggedInException.class)
    public ModelAndView handleUserExist(NotLoggedInException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(GameUnknownException.class)
    public ModelAndView handleUserExist(GameUnknownException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(NotAdminException.class)
    public ModelAndView handleUserExist(NotAdminException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }




}
