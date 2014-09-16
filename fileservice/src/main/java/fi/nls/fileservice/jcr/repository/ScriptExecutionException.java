package fi.nls.fileservice.jcr.repository;

/**
 * Exception to notify if executing javascript metadata update tasks failed
 * 
 */
@SuppressWarnings("serial")
public class ScriptExecutionException extends RuntimeException {

    public ScriptExecutionException() {

    }

    public ScriptExecutionException(String message) {
        super(message);
    }

    public ScriptExecutionException(Throwable cause) {
        super(cause);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
