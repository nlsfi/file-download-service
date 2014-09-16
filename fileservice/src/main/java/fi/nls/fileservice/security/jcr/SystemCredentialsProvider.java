package fi.nls.fileservice.security.jcr;

import javax.jcr.Credentials;

public final class SystemCredentialsProvider implements CredentialsProvider {

    private final Credentials credentials;

    public SystemCredentialsProvider(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

}
