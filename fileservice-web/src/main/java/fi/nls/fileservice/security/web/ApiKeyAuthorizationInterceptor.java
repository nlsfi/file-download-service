package fi.nls.fileservice.security.web;

import javax.jcr.Credentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.jcr.ApiKeyCredentials;

public class ApiKeyAuthorizationInterceptor extends
        TokenAuthorizationInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        // credentials already checked by another interceptor
        if (AuthorizationContextHolder.getCredentials() != null) {
            return true;
        }

        String apikey = request.getParameter("api_key");

        if (apikey != null && !"".equals(apikey)) {
            Credentials credentials = new ApiKeyCredentials(apikey,
                    mappedAccount);
            AuthorizationContextHolder.setCredentials(credentials);
            request.setAttribute("fi.nls.lapa.serviceid", "mtp");

            return true;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        return false;
    }
}
