package fi.nls.fileservice.security;

public interface AccessPolicyManager {

    public AccessPolicy getAccessPolicy(String username);

    public void saveAccessPolicy(AccessPolicy policy);

}
