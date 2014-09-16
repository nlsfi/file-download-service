package fi.nls.fileservice.security.jcr;

import javax.jcr.Credentials;

@SuppressWarnings("serial")
public abstract class ModeshapeCredentials implements Credentials {

    protected final String mappedAccount;

    public ModeshapeCredentials() {
        this.mappedAccount = null;
    }

    public ModeshapeCredentials(String mappedAccount) {
        this.mappedAccount = mappedAccount;
    }

    public String getMapped() {
        return this.mappedAccount;
    }

}
