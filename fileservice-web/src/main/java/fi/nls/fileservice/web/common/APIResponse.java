package fi.nls.fileservice.web.common;

public class APIResponse {

    private String msg;

    public APIResponse() {

    }

    public APIResponse(String message) {
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
