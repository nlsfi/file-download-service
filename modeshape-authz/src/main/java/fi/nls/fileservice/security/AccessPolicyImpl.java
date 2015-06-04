package fi.nls.fileservice.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AccessPolicyImpl implements AccessPolicy {

    private final String uid;
    private List<ACE> acis;

    private boolean isModified = true;

    public AccessPolicyImpl(String uid, List<ACE> acis) {
        this(uid, acis, false);
    }

    public AccessPolicyImpl(String uid, List<ACE> acis, boolean isNew) {
        this.uid = uid;
        this.acis = acis;
        this.isModified = isNew;
    }

    public String getUid() {
        return uid;
    }

    public List<ACE> getAcis() {
        return this.acis;
    }

    public boolean isModified() {
        return this.isModified;
    }

    @Override
    public boolean isAllowed(String path, String... actions) {

        if (acis != null) {
            for (ACE aci : acis) {
                if (path.startsWith(aci.getPath())) {
                    int aciPathLength = aci.getPath().length();
                    if (aciPathLength > 1
                            && path.length() > aci.getPath().length()) {
                        if (path.charAt(aci.getPath().length()) != '/') {
                            continue;
                        }
                    }

                    List<Privilege> privs = aci.getPrivileges();
                    for (String requestedAction : actions) {
                        for (Privilege privilege : privs) {
                            if (privilege.equals(requestedAction)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;

    }

    @Override
    public void removePrivilege(String path, Privilege... actions) {
        if (acis != null) {
            Iterator<ACE> acisIter = acis.iterator();
            while (acisIter.hasNext()) {
                ACE aci = acisIter.next();

                if (aci.getPath().equals(path)) {
                    List<Privilege> existingPrivileges = aci.getPrivileges();
                    if (existingPrivileges.removeAll(Arrays.asList(actions))) {
                        this.isModified = true;
                        if (existingPrivileges.size() == 0) {
                            acisIter.remove();
                        }
                    }

                }
            }
        }
    }

    @Override
    public void addPrivileges(String path, Privilege... actions) {
        if (this.acis == null) {
            this.acis = new ArrayList<ACE>();
        }

        boolean foundExisting = false;
        Iterator<ACE> acisIter = this.acis.iterator();
        while (acisIter.hasNext()) {
            ACE aci = acisIter.next();

            if (aci.getPath().equals(path)) {
                foundExisting = true;

                List<Privilege> existingPrivileges = aci.getPrivileges();
                if (existingPrivileges.size() == actions.length) {
                    for (Privilege p : actions) {
                        if (!existingPrivileges.contains(p)) {
                            aci.setPrivileges(Arrays.asList(actions));
                            this.isModified = true;
                            break;
                        }
                    }
                } else {
                    aci.setPrivileges(Arrays.asList(actions));
                    this.isModified = true;
                }
            }
        }

        if (!foundExisting) {
            ACE newACI = new ACE();
            newACI.setPath(path);
            newACI.setPrivileges(Arrays.asList(actions));
            this.acis.add(newACI);
            this.isModified = true;
        }
    }

}
