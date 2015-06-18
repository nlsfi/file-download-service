package fi.nls.fileservice.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AccessPolicyImplTest {
   
    @Test
    public void testAdd() {

        String uid = "system";

        List<ACE> acis = new ArrayList<ACE>(2);

        Set<Privilege> privileges = new HashSet<Privilege>();
        privileges.add(Privilege.READ);

        ACE mtk = new ACE("/tuotteet/maastotietokanta");
        mtk.setPrivileges(privileges);
        acis.add(mtk);

        AccessPolicy policy = new AccessPolicyImpl(uid, acis);
        policy.addPrivileges("/tuotteet/kuntajako", Privilege.READ);

        assertTrue(policy.isAllowed("/tuotteet/maastotietokanta/kaikki.zip", "read"));
        assertTrue(policy.isAllowed("/tuotteet/kuntajako/kaikki.zip", "read"));
    }

    @Test
    public void shouldHandleMultipleMatchingPathesWithDifferentPrivileges() {
        
        String uid = "system";

        AccessPolicyImpl policy = new AccessPolicyImpl(uid);
        policy.addPrivileges("/share", Privilege.REMOVE_CHILD_NODES);
        policy.addPrivileges("/share/de305d54-75b4-431b-adb2-eb6b9e546014", Privilege.READ);

        List<ChangeSet> changes = policy.getChanges();
        assertEquals(2, changes.size());
                
        assertTrue(policy.isAllowed("/share/de305d54-75b4-431b-adb2-eb6b9e546014/foo", "read"));
        assertTrue(policy.isAllowed("/share", "remove_child_nodes"));
        assertFalse(policy.isAllowed("/share", "read"));
                
    }
}
