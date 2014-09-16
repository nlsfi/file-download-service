package fi.nls.fileservice.jcr.repository;

import static org.junit.Assert.assertEquals;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.files.FileServiceImpl;

public class RepositoryServiceTest {

    private static TestRepositoryProvider provider;
    private static Repository repository;
    private static FileService service;

    @BeforeClass
    public static void setUp() throws Exception {
        provider = new TestRepositoryProvider();
        repository = provider.getRepositories();
        service = new FileServiceImpl(repository);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        provider.shutdown();
    }

    @Test
    public void testRead() throws LoginException, RepositoryException {
        DetachedNode detachedNode = service.getNode("/tuotteet/foo/bar");
        assertEquals("bar", detachedNode.getName());
        assertEquals("/tuotteet/foo/bar", detachedNode.getPath());
    }

    /*
     * 
     * TODO: FIX this to for with filesystemsource, test file needs a mixin..
     */
    /*
     * @Test public void testSetProperties() throws LoginException,
     * RepositoryException {
     * 
     * String time = Long.toString(System.currentTimeMillis()); String
     * propertyName = "nls:expires"; String path = "/foo";
     * 
     * DetachedProperty expiresProperty = new DetachedProperty();
     * expiresProperty.setMultiple(false);
     * expiresProperty.setName(propertyName); expiresProperty.setValue(time);
     * 
     * List<DetachedProperty> props = new ArrayList<DetachedProperty>(1);
     * props.add(expiresProperty);
     * 
     * service.saveProperties(path, props); DetachedNode detachedNode =
     * service.getNode(path); DetachedProperty detachedProperty =
     * detachedNode.getProperty(propertyName);
     * 
     * assertNotNull(detachedProperty); assertEquals(time,
     * detachedProperty.getValue());
     * 
     * }
     */

    // file deletion is broken in ModeShape 3.8.0?
    // https://issues.jboss.org/browse/MODE-2200
    /*
     * @Test public void testDelete() throws LoginException, RepositoryException
     * { service.delete("/tuotteet/foo/bar");
     * 
     * try { service.getNode("/tuotteet/foo/bar");
     * fail("Should have thrown NotFoundException after delete"); } catch
     * (NotFoundException pnfe) { // expected } }
     */

}
