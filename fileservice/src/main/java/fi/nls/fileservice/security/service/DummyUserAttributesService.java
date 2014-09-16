package fi.nls.fileservice.security.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy UserAttributes service that simply returns the passed uid in a Map. This
 * is to remove dependency to a LDAP directory for attribute query.
 */
public class DummyUserAttributesService implements UserAttributesProvider {

    @Override
    public Map<String, String> getUserDetails(String uid) {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("uid", uid);
        return attrs;
    }

}
