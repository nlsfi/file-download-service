package fi.nls.fileservice.security;

import java.util.Collection;

public interface AccessPolicy {

    boolean isAllowed(String path, String... actions);

    void addPrivileges(String path, Privilege... actions);

    void removePrivilege(String path, Privilege... actions);
    
    void removeAllPrivileges();
    
    void privilegesFrom(Collection<ACE> privileges);
}
