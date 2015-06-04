package fi.nls.fileservice.web.api.controller;

public class APIResponse {

    private final int code;
    private final String message;

    public APIResponse(String message) {
        this.code = 200; // OK
        this.message = message;
    }
    
    public APIResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }
}
