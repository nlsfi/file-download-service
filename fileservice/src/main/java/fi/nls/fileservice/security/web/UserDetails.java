package fi.nls.fileservice.security.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import fi.nls.fileservice.security.ACE;

public class UserDetails {

    @NotEmpty
    private String uid;
    private List<ACE> permissions;
    private Map<String, String> userAttributes;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        if (userAttributes != null) {
            return userAttributes.get("uid");
        }
        return uid;
    }

    public void setUserAttributes(Map<String, String> attrs) {
        this.userAttributes = attrs;
    }

    public Map<String, String> getUserAttributes() {
        return Collections.unmodifiableMap(userAttributes);
    }

    public void setPermissions(List<ACE> permissions) {
        this.permissions = permissions;
    }

    public List<ACE> getPermissions() {
        return permissions;
    }

}
