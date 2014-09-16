package fi.nls.fileservice.security.jcr;

import javax.jcr.Credentials;

@SuppressWarnings("serial")
public class ImpersonatingCredentials implements Credentials {

    private final String name;
    private final ExternalAuthenticationCredentials impersonated;

    public ImpersonatingCredentials(String name,
            ExternalAuthenticationCredentials impersonated) {
        this.name = name;
        this.impersonated = impersonated;
    }

    public String getName() {
        if (this.name == null) {
            return this.impersonated.getName();
        } else {
            return this.name;
        }
    }

    public ExternalAuthenticationCredentials getImpersonatedCredentials() {
        return this.impersonated;
    }

}
