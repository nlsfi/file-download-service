package fi.nls.fileservice.security;

import javax.jcr.Credentials;

public class AuthorizationContextHolder {

    private static final ThreadLocal<Credentials> credentialsLocal = new ThreadLocal<Credentials>();

    public static void setCredentials(Credentials credentials) {
        credentialsLocal.set(credentials);
    }

    public static Credentials getCredentials() {
        return credentialsLocal.get();
    }

    public static void unset() {
        credentialsLocal.remove();
    }
}
