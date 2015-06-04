package fi.nls.fileservice.web.api.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice(annotations=RestController.class)
public class APIErrorHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public void handleAPIException(Exception throwable,
            HttpServletResponse response) throws IOException {

        APIResponse apiResponse = null;
        
        if (throwable instanceof APIException) {
            APIException exception = (APIException)throwable;
            apiResponse = new APIResponse(exception.getErrorCode(), exception.getMessage());
            response.setStatus(((APIException)throwable).getErrorCode());
        } else {
            apiResponse = new APIResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    throwable.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        OutputStream out = response.getOutputStream();
        response.setContentType("application/json; charset=utf-8");
        mapper.writeValue(out, apiResponse);
    }
}
