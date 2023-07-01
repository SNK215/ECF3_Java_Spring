package ECF3.chessTournament.service.impl;

import ECF3.chessTournament.service.LoginService;
import ECF3.chessTournament.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    HttpSession httpSession;

    @Override
    public boolean login(User user) {
        httpSession.setMaxInactiveInterval(1800);
        httpSession.setAttribute("isLogged", true);
        httpSession.setAttribute("fullName", user.getFirstname() + " "+user.getLastname());
        httpSession.setAttribute("isAdmin", user.isAdmin());
        httpSession.setAttribute("userId", user.getId());
        return true;
    }

    @Override
    public boolean logout() {
        httpSession.setAttribute("isLogged", false);
        return true;
    }

    @Override
    public boolean isLogged() {
        return httpSession.getAttribute("isLogged") != null && (boolean)httpSession.getAttribute("isLogged") == true;
    }

    @Override
    public boolean isAdmin() {
        return httpSession.getAttribute("isLogged") != null && (boolean)httpSession.getAttribute("isAdmin") == true;
    }

    @Override
    public int getUserId() {
        return (int)httpSession.getAttribute("userId");
    }
}
