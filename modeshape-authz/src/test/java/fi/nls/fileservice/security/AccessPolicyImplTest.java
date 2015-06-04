package fi.nls.fileservice.security;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AccessPolicyImplTest {
   
    @Test
    public void testAdd() {

        String uid = "system";

        List<ACE> acis = new ArrayList<ACE>(2);

        List<Privilege> privileges = new ArrayList<Privilege>();
        privileges.add(Privilege.READ);

        ACE mtk = new ACE();
        mtk.setPath("/tuotteet/maastotietokanta");
        mtk.setPrivileges(privileges);
        acis.add(mtk);

        AccessPolicyImpl policy = new AccessPolicyImpl(uid, acis);
        policy.addPrivileges("/tuotteet/kuntajako", Privilege.READ);

        assertTrue(policy.isAllowed("/tuotteet/maastotietokanta/kaikki.zip", "read"));
        assertTrue(policy.isAllowed("/tuotteet/kuntajako/kaikki.zip", "read"));
    }

    @Test
    public void shouldHandleMultipleMatchingPathesWithDifferentPrivileges() {
        
        String uid = "system";

        List<ACE> acis = new ArrayList<ACE>(2);

        List<Privilege> privileges = new ArrayList<Privilege>();
        privileges.add(Privilege.REMOVE_CHILD_NODES);

        ACE mtk = new ACE();
        mtk.setPath("/share");
        mtk.setPrivileges(privileges);
        acis.add(mtk);

        AccessPolicyImpl policy = new AccessPolicyImpl(uid, acis);
        policy.addPrivileges("/share/de305d54-75b4-431b-adb2-eb6b9e546014", Privilege.READ);

        assertTrue(policy.isAllowed("/share/de305d54-75b4-431b-adb2-eb6b9e546014/foo", "read"));
        assertTrue(policy.isAllowed("/share", "remove_child_nodes"));
        assertFalse(policy.isAllowed("/share", "read"));
    }
}
