package fi.nls.fileservice.web.api.controller;

@SuppressWarnings("serial")
public class APIException extends RuntimeException {

    private int errorCode;

    public APIException() {
        // TODO Auto-generated constructor stub
    }

    public APIException(int code, String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public APIException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = code;
    }

    public APIException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public APIException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public int getErrorCode() {
        return this.errorCode;
    }

}
