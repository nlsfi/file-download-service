package fi.nls.fileservice.security;

public interface AccessPolicy {

    boolean isAllowed(String path, String... actions);

    void addPrivileges(String path, Privilege... actions);

    void removePrivilege(String path, Privilege... actions);
}
