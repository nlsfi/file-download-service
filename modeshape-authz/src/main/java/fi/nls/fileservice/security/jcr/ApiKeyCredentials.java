package fi.nls.fileservice.security.jcr;

@SuppressWarnings("serial")
public final class ApiKeyCredentials extends ModeshapeCredentials {

    private final String apiKey;

    public ApiKeyCredentials(String apiKey, String mappedAccount) {
        super(mappedAccount);
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }

}
