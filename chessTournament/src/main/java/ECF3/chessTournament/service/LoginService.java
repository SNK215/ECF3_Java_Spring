package ECF3.chessTournament.Service;

import ECF3.chessTournament.entity.User;

public interface LoginService {
    public boolean login(User user);
    public boolean logout();
    public boolean isLogged();
    public boolean isAdmin();
    public int getUserId();
}
