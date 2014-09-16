package fi.nls.modeshape.jcr.security;

import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthorizationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.modeshape.jcr.value.Path;

public class AllowAllAuthenticationProvider implements AuthorizationProvider,
        SecurityContext {

    private String username;

    public AllowAllAuthenticationProvider(String username) {
        this.username = username;
    }

    @Override
    public boolean hasPermission(ExecutionContext context,
            String repositoryName, String repositorySourceName,
            String workspaceName, Path path, String... actions) {
        return true;
    }

    @Override
    public String getUserName() {
        return this.username;
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isAnonymous() {
        // TODO Auto-generated method stub
        return false;
    }

}
