package fi.nls.fileservice.security.service;

import java.util.Map;

public interface UserAttributesProvider {

    public Map<String, String> getUserDetails(String uid);
}
