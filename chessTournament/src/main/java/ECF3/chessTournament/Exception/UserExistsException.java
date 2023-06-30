package ECF3.chessTournament.Exception;

public class UserExistsException extends Exception{
    public UserExistsException() {
        super("User already exists");
    }
}
