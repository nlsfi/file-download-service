package fi.nls.fileservice.jcr.repository;

import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthorizationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.modeshape.jcr.value.Path;

public class AllowAllAuthorizationProvider implements AuthorizationProvider,
        SecurityContext {

    @Override
    public boolean hasPermission(ExecutionContext arg0, String arg1,
            String arg2, String arg3, Path arg4, String... arg5) {
        return true;
    }

    @Override
    public String getUserName() {
        // must not return null here, otherwise
        // modeshape throws exception when adding nodes..
        return "junit tester";
    }

    @Override
    public boolean hasRole(String arg0) {
        // TODO Auto-generated method stub
        return true;
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
