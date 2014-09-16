package fi.nls.fileservice.security.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.security.ACE;
import fi.nls.fileservice.security.AccessPolicyImpl;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.Privilege;
import fi.nls.fileservice.security.web.UserDetails;

public class UserServiceImpl implements UserService {

    private UserAttributesProvider uas;
    private AccessPolicyManager accessPolicyManager;

    // TODO: find another way to do this
    private String datasetMetaPath;
    private DatasetService datasetService;

    public UserServiceImpl(UserAttributesProvider uas,
            AccessPolicyManager accessPolicyManager,
            DatasetService datasetService, String datasetMetaPath) {
        this.uas = uas;
        this.accessPolicyManager = accessPolicyManager;
        this.datasetService = datasetService;
        this.datasetMetaPath = datasetMetaPath;
    }

    @Override
    public UserDetails getUserDetails(String uid) {

        Map<String, String> userAttributes = uas.getUserDetails(uid);
        if (userAttributes == null || userAttributes.isEmpty()) {
            // user doesn't exist
            return null;
        }

        UserDetails details = new UserDetails();
        details.setUserAttributes(userAttributes);

        AccessPolicyImpl policy = (AccessPolicyImpl) accessPolicyManager.getAccessPolicy(uid);
        if (policy != null) {
            List<ACE> acis = policy.getAcis();

            // filter implicit permissions to dataset metadata
            // these are set automatically, if user has permissions to
            // corresponding dataset files
            Iterator<ACE> iter = acis.iterator();
            while (iter.hasNext()) {
                ACE aci = iter.next();
                if (aci.getPath().startsWith(datasetMetaPath)) {
                    iter.remove();
                }
            }

            details.setUid(userAttributes.get("uid"));
            details.setPermissions(acis);
            return details;

        } else {
            details.setPermissions(new ArrayList<ACE>());
        }

        return details;
    }

    @Override
    public void savePermissions(UserDetails permissions) {

        List<Dataset> datasets = datasetService.getAllDatasets();

        List<ACE> acis = permissions.getPermissions();
        if (acis == null) { // empty, remove all
            acis = new ArrayList<ACE>();
        }
        List<ACE> addedAcis = new ArrayList<ACE>();
        Iterator<ACE> iter = acis.iterator();
        while (iter.hasNext()) {
            ACE aci = iter.next();
            if (aci.getPath() == null) {
                iter.remove();
            } else {
                // remove possible trailing '/'
                if (aci.getPath().endsWith("/") && aci.getPath().length() > 1) {
                    aci.setPath(aci.getPath().substring(0,
                            aci.getPath().length() - 1));
                }

                // we only support read privileges for external users for now
                List<Privilege> privileges = new ArrayList<Privilege>(1);
                privileges.add(Privilege.READ);
                aci.setPrivileges(privileges);

                // if a given path references a dataset, we also implicitly add
                // read permission to corresponding dataset's metadata
                // this is required for atom feeds to function
                for (Dataset dataset : datasets) {
                    if (aci.getPath() != null) {
                        if (aci.getPath().startsWith(dataset.getPath())) {
                            ACE newAci = new ACE();
                            newAci.setPath(dataset.getMetadataPath());
                            newAci.setPrivileges(privileges);
                            addedAcis.add(newAci);
                        }
                    }
                }
            }
        }

        if (!addedAcis.isEmpty()) {
            acis.addAll(addedAcis);
        }

        AccessPolicyImpl policy = new AccessPolicyImpl(permissions.getUid(), acis, true);
        accessPolicyManager.saveAccessPolicy(policy);

    }

}
