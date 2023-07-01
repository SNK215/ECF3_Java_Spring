package ECF3.chessTournament.exception;

public class NotAdminException extends Exception{
    public NotAdminException() {
        super("Administrator required");
    }
}
