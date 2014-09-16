package fi.nls.fileservice.security;

public class ExpiredOrNonexistingTokenException extends RuntimeException {

    public ExpiredOrNonexistingTokenException(String message) {
        super(message);
    }

}
