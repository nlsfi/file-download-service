package fi.nls.modeshape.jcr.security;

import java.util.Map;

import javax.jcr.Credentials;

import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.PolicyAccessException;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;
import fi.nls.fileservice.security.jcr.ImpersonatingCredentials;
import fi.nls.fileservice.security.pgsql.PGAccessPolicyManager;

/**
 * ModeShape AuthenticationProvider, that relies on user data provided by
 * external authentication system such as WebSSO (Apache HTTPD auth etc.)
 */
public class ExternalAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory
            .getLogger(ExternalAuthenticationProvider.class);

    private AccessPolicyManager accessPolicyManager;
    public String dataSourceName;

    public ExternalAuthenticationProvider() {
        // Passing these values doesn't work in ModeShape 3, so we have no
        // choice but to hardcode them (BUG?)
        // we should use the method setDataSourceName..
        String dataSource = "java:comp/env/jdbc/tiepaldb";
        PGAccessPolicyManager accessPolicyManager = new PGAccessPolicyManager();
        accessPolicyManager.setDataSourceName(dataSource);
        this.accessPolicyManager = accessPolicyManager;
    }
    
    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        PGAccessPolicyManager accessPolicyManager = new PGAccessPolicyManager();
        accessPolicyManager.setDataSourceName(dataSourceName);
        this.accessPolicyManager = accessPolicyManager;
    }

    @Override
    public ExecutionContext authenticate(Credentials credentials,
            String repositoryName, String workspaceName,
            ExecutionContext repositoryContext,
            Map<String, Object> sessionAttributes) {

        String username = null;
        
        if (credentials != null
                && credentials instanceof ImpersonatingCredentials) {
            ImpersonatingCredentials impCreds = (ImpersonatingCredentials) credentials;
            username = impCreds.getName();
        } else if (credentials != null
                && credentials instanceof ExternalAuthenticationCredentials) {
            ExternalAuthenticationCredentials creds = ((ExternalAuthenticationCredentials) credentials);
            username = creds.getName();
        }

        if (username != null) {
            try {
                AccessPolicy policy = accessPolicyManager
                        .getAccessPolicy(username);
                if (policy != null) {
                    return repositoryContext
                            .with(new PathBasedAuthorizationProvider(username,
                                    policy));
                }
            } catch (PolicyAccessException e) {
                logger.error("Error during authentication", e);
            }
        }
        return null;
    }

}
