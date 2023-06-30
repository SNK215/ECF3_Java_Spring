package ECF3.chessTournament.exception;

public class UserExistsException extends Exception{
    public UserExistsException() {
        super("User already exists");
    }
}
