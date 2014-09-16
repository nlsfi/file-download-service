package fi.nls.fileservice.security.jcr;

@SuppressWarnings("serial")
public final class TemporaryTokenCredentials extends ModeshapeCredentials {

    private final String token;

    public TemporaryTokenCredentials(String token, String mappedAccount) {
        super(mappedAccount);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
