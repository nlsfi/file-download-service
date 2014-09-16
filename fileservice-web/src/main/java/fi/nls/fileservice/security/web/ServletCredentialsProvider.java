package fi.nls.fileservice.security.web;

import javax.jcr.Credentials;
import javax.servlet.http.HttpServletRequest;

import fi.nls.fileservice.security.jcr.CredentialsProvider;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;

public final class ServletCredentialsProvider implements CredentialsProvider {

    private ExternalAuthenticationCredentials credentials;

    public ServletCredentialsProvider(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            throw new IllegalArgumentException(
                    "No principal found in servlet request.");
        }
        this.credentials = new ExternalAuthenticationCredentials(request
                .getUserPrincipal().getName(), null);
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

}
