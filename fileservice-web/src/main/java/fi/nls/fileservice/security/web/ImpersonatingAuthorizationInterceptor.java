package fi.nls.fileservice.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.jcr.CredentialsProvider;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;
import fi.nls.fileservice.security.jcr.ImpersonatingCredentials;

public class ImpersonatingAuthorizationInterceptor extends
        BaseAuthorizationInterceptor {

    private final CredentialsProvider credentialsProvider;

    public ImpersonatingAuthorizationInterceptor(
            CredentialsProvider credentialsProvider) {
        super();
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String name = null;
        if (request.getUserPrincipal() != null) {
            name = request.getUserPrincipal().getName();
        }
        if (name == null) {
            name = request.getHeader("uid");
        }

        AuthorizationContextHolder.setCredentials(new ImpersonatingCredentials(
                name, (ExternalAuthenticationCredentials) credentialsProvider
                        .getCredentials()));

        return true;
    }

}
