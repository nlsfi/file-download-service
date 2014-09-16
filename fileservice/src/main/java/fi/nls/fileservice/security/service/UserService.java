package fi.nls.fileservice.security.service;

import fi.nls.fileservice.security.web.UserDetails;

public interface UserService {

    UserDetails getUserDetails(String uid);

    void savePermissions(UserDetails permissions);

}
