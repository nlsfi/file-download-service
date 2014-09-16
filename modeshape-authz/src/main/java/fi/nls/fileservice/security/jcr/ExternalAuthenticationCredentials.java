package fi.nls.fileservice.security.jcr;

import java.security.Principal;

import javax.jcr.Credentials;

@SuppressWarnings("serial")
public final class ExternalAuthenticationCredentials implements Credentials,
        Principal {

    private final String uid;
    private final String commonName;

    public ExternalAuthenticationCredentials(String uid, String commonName) {
        this.uid = uid;
        this.commonName = commonName;
    }

    @Override
    public String getName() {
        return uid;
    }

    public String getCommonName() {
        return commonName;
    }
}
