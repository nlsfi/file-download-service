package fi.nls.fileservice.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AccessPolicyImpl implements AccessPolicy {
    
    private final String uid;
    private final List<ACE> acis;
    private final List<ChangeSet> changes;
    
    public AccessPolicyImpl(String uid) {
        this(uid, new ArrayList<ACE>());
    }
    
    public AccessPolicyImpl(String uid, List<ACE> acis) {
        this.uid = uid;
        this.acis = acis;
        this.changes = new ArrayList<ChangeSet>();
    }

    public String getUid() {
        return uid; 
    }

    public List<ACE> getAcis() {
        return this.acis;
    }

    public boolean isModified() {
        return this.changes.size() > 0;
    }

    @Override
    public boolean isAllowed(String path, String... actions) {

        if (acis != null) {
            for (ACE aci : acis) {
                if (path.startsWith(aci.getPath())) {
                    int aciPathLength = aci.getPath().length();
                    if (aciPathLength > 1 && path.length() > aci.getPath().length()) {
                        if (path.charAt(aci.getPath().length()) != '/') {
                            continue;
                        }
                    }

                    Collection<Privilege> privs = aci.getPrivileges();
                    for (String requestedAction : actions) {
                        if (privs.contains(Privilege.forName(requestedAction))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void removePrivilege(String path, Privilege... actions) {
        if (this.acis != null) {
            Iterator<ACE> acisIter = this.acis.iterator();
            while (acisIter.hasNext()) {
                ACE aci = acisIter.next();

                if (aci.getPath().equals(path)) {
                    Collection<Privilege> existingPrivileges = aci.getPrivileges();
                    for (Privilege p : actions) {
                        if (existingPrivileges.contains(p)) {
                            existingPrivileges.remove(p);
                        }
                    }
                    if (existingPrivileges.size() == 0) {
                        this.changes.add(new ChangeSet(aci, ChangeSet.ChangeType.REMOVE));
                        acisIter.remove();
                    } else {
                        this.changes.add(new ChangeSet(aci, ChangeSet.ChangeType.MODIFY));
                    }
                }
            }
        }
    }

    @Override
    public void addPrivileges(String path, Privilege... actions) {

        boolean foundExisting = false;
        Iterator<ACE> acisIter = this.acis.iterator();
        while (acisIter.hasNext()) {
            ACE ace = acisIter.next();

            if (ace.getPath().equals(path)) {
                foundExisting = true;

                Collection<Privilege> existingPrivileges = ace.getPrivileges();
                for (Privilege p : actions) {
                    existingPrivileges.add(p);
                }
                this.changes.add(new ChangeSet(ace, ChangeSet.ChangeType.MODIFY));
            }
        }

        if (!foundExisting) {
            ACE newAce = new ACE(path);
            for (Privilege p : actions) {
                newAce.getPrivileges().add(p);
            }
            this.acis.add(newAce);
            this.changes.add(new ChangeSet(newAce, ChangeSet.ChangeType.ADD));
        }
    }
    
    public List<ChangeSet> getChanges() {
        return this.changes;
    }

    @Override
    public void removeAllPrivileges() {
        Iterator<ACE> acisIter = this.acis.iterator();
        while(acisIter.hasNext()) {
            ACE ace = acisIter.next();
            acisIter.remove();
            this.changes.add(new ChangeSet(ace, ChangeSet.ChangeType.REMOVE));
        }
    }

    @Override
    public void privilegesFrom(Collection<ACE> newACEs) {
        Iterator<ACE> currentACEs = this.acis.iterator();
        while(currentACEs.hasNext()) {
            ACE ace = currentACEs.next();
            boolean present= false;
            for (ACE newACE : newACEs) {
                if (ace.getPath().equals(newACE.getPath())) {
                    present = true;
                    if (!ace.getPrivileges().equals(newACE.getPrivileges())) {
                        ace.setPrivileges((Set<Privilege>)newACE.getPrivileges());
                        this.changes.add(new ChangeSet(ace, ChangeSet.ChangeType.MODIFY));
                    }
                }
            }
            if (!present) {
                currentACEs.remove();
                this.changes.add(new ChangeSet(ace, ChangeSet.ChangeType.REMOVE));
            }
        }
        
        for (ACE newACE : newACEs) {
            boolean isNew = true;
            for (ACE ace : this.acis) {
                if (ace.getPath().equals(newACE.getPath())) {
                    isNew = false;
                }
            }
            if (isNew) {
                this.acis.add(newACE);
                this.changes.add(new ChangeSet(newACE, ChangeSet.ChangeType.ADD));
            }
                
        }
        
    }

}
