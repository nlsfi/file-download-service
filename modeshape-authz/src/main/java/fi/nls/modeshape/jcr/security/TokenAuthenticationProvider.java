package fi.nls.modeshape.jcr.security;

import java.util.Map;

import javax.jcr.Credentials;

import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.PolicyAccessException;
import fi.nls.fileservice.security.jcr.ApiKeyCredentials;
import fi.nls.fileservice.security.jcr.TemporaryTokenCredentials;
import fi.nls.fileservice.security.pgsql.PGAccessPolicyManager;
import fi.nls.fileservice.security.pgsql.PGTokenValidator;

/**
 * ModeShape AutheticatinProvider that "authenticates" the user by a temporary
 * token
 * 
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory
            .getLogger(TokenAuthenticationProvider.class);

    protected PGAccessPolicyManager accessPolicyManager;
    protected PGTokenValidator tokenValidator;

    protected String dataSourceName;

    public TokenAuthenticationProvider() {
        this.accessPolicyManager = new PGAccessPolicyManager();
        this.tokenValidator = new PGTokenValidator();

        // FIXME Passing these values doesn't work in ModeShape 3, so we have no
        // choice but to hardcode them (BUG?)
        this.accessPolicyManager
                .setDataSourceName("java:comp/env/jdbc/tiepaldb");
        this.tokenValidator.setDataSourceName("java:comp/env/jdbc/tiepaldb");
    }

    public void setDataSourceName(String dataSource) {
        accessPolicyManager.setDataSourceName(dataSource);
        tokenValidator.setDataSourceName(dataSource);
    }

    @Override
    public ExecutionContext authenticate(Credentials credentials,
            String repositoryName, String workspaceName,
            ExecutionContext repositoryContext,
            Map<String, Object> sessionAttributes) {

        String token = null;
        AccessPolicy policy = null;

        try {

            // we use separate classes for APIkeys and credentials to disallow
            // using for temporary token as apikeys

            if (credentials instanceof ApiKeyCredentials) {
                ApiKeyCredentials apikeyCreds = (ApiKeyCredentials) credentials;
                token = apikeyCreds.getApiKey();
                String mappedAccount = apikeyCreds.getMapped();

                if (tokenValidator.isApiKeyValid(token)
                        && mappedAccount != null) {
                    // map token to "open data" account
                    policy = accessPolicyManager.getAccessPolicy(mappedAccount);
                }

            } else if (credentials instanceof TemporaryTokenCredentials) {
                TemporaryTokenCredentials tokenCreds = (TemporaryTokenCredentials) credentials;
                token = tokenCreds.getToken();
                String mappedAccount = tokenCreds.getMapped();

                if (tokenValidator.isTokenValid(token) && mappedAccount != null) {
                    // map token to "open data" account
                    policy = accessPolicyManager.getAccessPolicy(mappedAccount);
                }
            } else {
                // noop, unrecognized credentials, pass on to other
                // AuthenticationProviders
                return null;
            }
             

            if (policy != null) {
                SecurityContext securityContext = new PathBasedAuthorizationProvider(
                        token, policy);
                return repositoryContext.with(securityContext);
            }

            logger.warn("Expired or nonexisting token/apikey: " + token);

        } catch (PolicyAccessException e) {
            logger.warn("Error during token authentication", e);
        }

        return null;
    }

}
