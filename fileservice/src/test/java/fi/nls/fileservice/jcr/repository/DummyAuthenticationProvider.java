package fi.nls.fileservice.jcr.repository;

import java.util.Map;

import javax.jcr.Credentials;

import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;

public class DummyAuthenticationProvider implements AuthenticationProvider {

    @Override
    public ExecutionContext authenticate(Credentials arg0, String arg1,
            String arg2, ExecutionContext repositoryContext,
            Map<String, Object> arg4) {
        return repositoryContext.with(new AllowAllAuthorizationProvider());
    }

}
