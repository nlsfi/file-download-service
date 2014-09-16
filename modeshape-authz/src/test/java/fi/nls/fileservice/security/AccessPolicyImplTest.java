package fi.nls.fileservice.security;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AccessPolicyImplTest {
    /*
     * @Test public void testPolicy() {
     * 
     * String uid = "system"; List<ACE> acis = new ArrayList<ACE>(2);
     * 
     * ACE krk = new ACE(); krk.setPath("/tuotteet/kiinteistorekisteri");
     * 
     * 
     * List<Privilege> privileges = new ArrayList<Privilege>();
     * privileges.add(Privilege.READ);
     * 
     * krk.setPrivileges(privileges);
     * 
     * acis.add(krk);
     * 
     * AccessPolicyImpl policy = new AccessPolicyImpl(uid,acis);
     * 
     * assertTrue(policy.isAllowed("/tuotteet/kiinteistorekisteri/krk.zip",
     * "read"));
     * assertFalse(policy.isAllowed("/tuotteet/kiinteistorekisterikartta/krk.zip"
     * , "read")); assertFalse(policy.isAllowed("/tuotteet/", "read"));
     * assertFalse(policy.isAllowed("/tuotteet", "read")); }
     * 
     * @Test public void testRoot() {
     * 
     * String uid = "system"; List<ACE> acis = new ArrayList<ACE>(2);
     * 
     * List<Privilege> privileges = new ArrayList<Privilege>();
     * privileges.add(Privilege.READ);
     * 
     * ACE krk = new ACE(); krk.setPath("/"); krk.setPrivileges(privileges);
     * 
     * acis.add(krk);
     * 
     * AccessPolicyImpl policy = new AccessPolicyImpl(uid,acis);
     * 
     * assertTrue(policy.isAllowed("/tuotteet/maastotietokanta", "read"));
     * assertTrue(policy.isAllowed("/", "read")); }
     * 
     * @Test public void testRemove() {
     * 
     * String uid = "system"; List<ACE> acis = new ArrayList<ACE>(2);
     * 
     * List<Privilege> privileges = new ArrayList<Privilege>();
     * privileges.add(Privilege.READ); privileges.add(Privilege.ADD_NODE);
     * privileges.add(Privilege.SET_PROPERTY);
     * 
     * ACE krk = new ACE(); krk.setPath("/files/share");
     * krk.setPrivileges(privileges);
     * 
     * acis.add(krk);
     * 
     * AccessPolicyImpl policy = new AccessPolicyImpl(uid,acis);
     * 
     * policy.removePrivilege("/files/share", Privilege.SET_PROPERTY,
     * Privilege.ADD_NODE);
     * 
     * assertTrue(policy.isAllowed("/files/share", "read"));
     * assertFalse(policy.isAllowed("/files/share", "add_node"));
     * assertFalse(policy.isAllowed("/files/share", "set_property")); }
     */

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

        assertTrue(policy.isAllowed("/tuotteet/maastotietokanta/kaikki.zip",
                "read"));
        assertTrue(policy.isAllowed("/tuotteet/kuntajako/kaikki.zip", "read"));
    }

}
