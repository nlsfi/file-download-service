package fi.nls.modeshape.jcr.security;

import java.util.Arrays;

import org.modeshape.jcr.ModeShapePermissions;
import org.modeshape.jcr.ModeShapeRoles;
import org.modeshape.jcr.security.AdvancedAuthorizationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.modeshape.jcr.value.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.AccessPolicy;

/**
 * AuthorizationProvider for ModeShape that provides node based access control
 */
public final class PathBasedAuthorizationProvider implements
        AdvancedAuthorizationProvider, SecurityContext {

    private static final Logger logger = LoggerFactory
            .getLogger(PathBasedAuthorizationProvider.class);

    private final String userName;
    private final AccessPolicy policy;

    public PathBasedAuthorizationProvider(String userName, AccessPolicy policy) {
        this.userName = userName;
        this.policy = policy;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public boolean hasRole(String role) {
        // FIXME: workaround against this role check:
        // https://github.com/ModeShape/modeshape/blob/modeshape-3.8.0.Final/modeshape-jcr/src/main/java/org/modeshape/jcr/JcrRepository.java#L689
        // not sure how this should really be implemented
        if (ModeShapeRoles.READWRITE.equals(role)) {
            return true;
        }

        // shouldn't be called since we've implemented AuthorizationProvider
        return false;
    }

    @Override
    public void logout() {
        // noop
    }

    @Override
    public boolean hasPermission(Context context, Path path, String... actions) {

        // must allow read request for empty path
        // see
        // org.modeshape.jcr.JcrSession.checkPermission(workspaceName,path,action);
        // otherwise java.security.AccessControlException: Permission denied to
        // perform actions "read" on path <unknown>
        // is thrown during repository.login

        // ModeShape 3 / import requires also check for path == null &&
        // action[0] == register_namespace
        if (path == null
                && (actions != null && actions.length > 0 && (ModeShapePermissions.READ
                        .equals(actions[0]) || ModeShapePermissions.REGISTER_NAMESPACE
                        .equals(actions[0])))) {
            return true;
        }

        // ModeShape returns paths in format /{}dir1/{}dir2/.. let's remove
        // those extra characters
        // FIXME: should optimize and store paths in the same format
        String strPath = path.getString().replaceAll("\\{}", "");

        boolean authznResult = policy.isAllowed(strPath, actions);
        logger.debug("Authorization result for: " + userName + " : " + strPath
                + " : " + Arrays.toString(actions) + " -> " + authznResult);
        return authznResult;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

}
