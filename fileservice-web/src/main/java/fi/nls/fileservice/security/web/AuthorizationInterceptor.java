package fi.nls.fileservice.security.web;

import javax.jcr.Credentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;

public class AuthorizationInterceptor extends BaseAuthorizationInterceptor {

    private String userNameHeader;

    public void setUserNameHeader(String header) {
        this.userNameHeader = header;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String uid = null;
        if (request.getUserPrincipal() != null) {
            uid = request.getUserPrincipal().getName();
        }
        if (uid == null && this.userNameHeader != null) {
            uid = request.getHeader(this.userNameHeader);
        }
        if (uid != null) {
            Credentials credentials = new ExternalAuthenticationCredentials(
                    uid, null);
            AuthorizationContextHolder.setCredentials(credentials);
            return true;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

}
