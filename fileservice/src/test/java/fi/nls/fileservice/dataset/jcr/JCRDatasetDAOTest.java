package fi.nls.fileservice.dataset.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetGridDefinition;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.jcr.repository.TestRepositoryProvider;

public class JCRDatasetDAOTest {

    private static TestRepositoryProvider provider;
    private static Repository repository;
    private static JCRDatasetDAO dao;
    //private static ApplicationContext context;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUp() throws RepositoryException, IOException,
            SAXException {
        provider = new TestRepositoryProvider();
        repository = provider.getRepositories();

        //context = new ClassPathXmlApplicationContext("mappings.xml");
        //context.getResource("crsDefinitions");

        Map<String,CrsDefinition> defs = new HashMap<String,CrsDefinition>();
        
        CrsDefinition def = new CrsDefinition();
        def.setCrsId("etrs-tm35fin");
        def.setEpsgId("EPSG:3067");
        def.setInspireUri("http://www.opengis.net/def/crs/EPSG/0/3067");
        def.setInspireLabel("ETRS89 / ETRS-TM35FIN");
        
        fi.nls.fileservice.dataset.DatasetGridDefinition gridDef =
        		new fi.nls.fileservice.dataset.DatasetGridDefinition();
        gridDef.setGridSize("6x6");
        gridDef.setCrs("etrs-tm35fin");
        Map<String,DatasetGridDefinition> gds = new HashMap<String,DatasetGridDefinition>();
        gds.put(gridDef.getGridSize(), gridDef);
        def.setGrids(gds);
        
        /*
        <property name="crsId" value="etrs-tm35fin"/>
        <property name="epsgId" value="EPSG:3067"/> 
        <property name="inspireUri" value="http://www.opengis.net/def/crs/EPSG/0/3067"/>
        <property name="inspireLabel" value="ETRS89 / ETRS-TM35FIN"/>
        <property name="grids">
            <map>
                <entry key="3x3">
                    <bean class="fi.nls.fileservice.dataset.DatasetGridDefinition">
                        <property name="gridSize" value="3x3"/>
                        <property name="crs" value="etrs-tm35fin"/>
                        <property name="minGridScale" value="500"/>
                        <property name="maxGridScale" value="200000"/>
                    </bean>
                </entry>
        */
        defs.put(def.getCrsId(), def);
        
        
        dao = new JCRDatasetDAO("/meta/", defs);
                //(Map<String, CrsDefinition>) context.getBean("crsDefinitions"));

        // Session session = repository.login();

    }

    @After
    public void resetState() throws LoginException, RepositoryException {
        Session session = repository.login();
        Node root = session.getNode("/meta");
        if (root.hasNodes()) {
            NodeIterator iterator = root.getNodes();
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                if (!"jcr:system".equals(node.getName())) {
                    node.remove();
                }
            }
        }
        session.save();
    }

    @AfterClass
    public static void clearRepository() throws LoginException,
            RepositoryException {
        /*
         * Session session = repository.login(); Node root =
         * session.getNode("/meta"); if (root.hasNodes()) { NodeIterator
         * iterator = root.getNodes(); while(iterator.hasNext()) { Node node =
         * iterator.nextNode(); if (!"jcr:system".equals(node.getName())) {
         * node.remove(); } } } session.save();
         */
        provider.shutdown();

    }

    public static Dataset getTestDataset() {
        Dataset dataset = new Dataset();
        dataset.setName("orto");
        dataset.setFileIdentifier("b20a360b-1734-41e5-a5b8-0e90dd9f2af3");
        dataset.setPath("/tuotteet/orto");
        // dataset.getFormats().add("JPEG2000");
        dataset.getTranslatedTitles().put("fi", "Ortoilmakuva");
        dataset.getTranslatedTitles().put("sv", "Ortobild");
        dataset.getTranslatedTitles().put("en", "Orthophoto");
        dataset.setSpatialDatasetIdentifierCode("1000244");
        dataset.setSpatialDatasetIdentifierNamespace("http://www.maanmittauslaitos.fi");
        return dataset;
    }

    @Test
    public void doTestStore() throws ValueFormatException,
            PathNotFoundException, RepositoryException {

        Dataset dataset = getTestDataset();
        Session session = repository.login(new Credentials() {
        });
        dao.saveDataset(dataset, session);
        session.save();

        List<Dataset> datasets = dao.getDatasets(false, session);
        assertEquals(1, datasets.size());
        session.logout();

        session = repository.login();

        dataset = dao.getDatasetById("orto", session);
        assertEquals("orto", dataset.getName());
        assertEquals("b20a360b-1734-41e5-a5b8-0e90dd9f2af3",
                dataset.getFileIdentifier());
        assertEquals("/tuotteet/orto", dataset.getPath());
        assertEquals("1000244", dataset.getSpatialDatasetIdentifierCode());
        assertEquals("http://www.maanmittauslaitos.fi",
                dataset.getSpatialDatasetIdentifierNamespace());
        assertEquals(3, dataset.getTranslatedTitles().size());
        // assertEquals(0, dataset.getFormats().size());
        // assertEquals("JPEG2000", dataset.getFormats().get(0));
        assertFalse(dataset.isPublished());
        assertEquals(Licence.RESTRICTED, dataset.getLicence());
        assertTrue(dataset.getVersions().isEmpty());
        session.logout();
    }

    /*
     * @Test public void doTestDatasetMove() throws ValueFormatException,
     * PathNotFoundException, RepositoryException { Session session =
     * repository.login();
     * 
     * Dataset dataset = getTestDataset(); String originalId =
     * dataset.getName(); String newId = "abracadabra";
     * 
     * dao.saveDataset(dataset, session); session.save(); Dataset
     * acquiredDataset = dao.getDatasetById(originalId, session);
     * acquiredDataset.setName(newId);
     * acquiredDataset.setPreviousId(originalId);
     * dao.saveDataset(acquiredDataset, session); session.save();
     * 
     * try { dao.getDatasetById(originalId, session);
     * fail("Should have thrown PathNotFoundException"); } catch
     * (javax.jcr.PathNotFoundException pnfe) { // OK } Dataset datasetWithNewId
     * = dao.getDatasetById(newId, session); assertEquals(newId,
     * datasetWithNewId.getName()); assertEquals(dataset.getPath(),
     * datasetWithNewId.getPath());
     * assertEquals(dataset.getTranslatedTitles().get("fi"),
     * datasetWithNewId.getTranslatedTitles().get("fi"));
     * assertEquals(dataset.isPublished(), datasetWithNewId.isPublished()); }
     */

    @Test
    public void testSaveMultiFileDatasetVersion() throws LoginException,
            RepositoryException {
        Dataset dataset = getTestDataset();
        Session session = repository.login();
        dao.saveDataset(dataset, session);

        DatasetVersion version = new DatasetVersion();
        version.setName("orto");
        version.getTranslatedTitles().put("fi", "väriorto");
        version.getTranslatedTitles().put("sv", "orto i färg");
        version.getTranslatedTitles().put("en", "ortho in colour");
        version.setWmsLayer("ortokuva");
        version.setWmsMinScale("500");
        version.setWmsMaxScale("20000");

        DatasetGridDefinition gridDef = new DatasetGridDefinition();
        gridDef.setCrs("etrs-tm35fin");
        gridDef.setGridSize("6x6");
        version.getGridDefinitions().add(gridDef);
        dao.saveDatasetVersion(dataset.getName(), version, session);

        session.save();

        version = dao.getDatasetVersion(dataset.getName(), "orto", session);
        assertNotNull(version.getDataset());
        assertEquals(dataset.getName(), version.getDataset().getName());
        assertEquals(dataset.getFileIdentifier(), version.getDataset()
                .getFileIdentifier());
        assertEquals("orto", version.getName());
        assertEquals("ortokuva", version.getWmsLayer());
        assertEquals("500", version.getWmsMinScale());
        assertEquals("20000", version.getWmsMaxScale());
        assertEquals(1, version.getGridDefinitions().size());
        assertEquals("etrs-tm35fin", version.getGridDefinitions().get(0).getCrs());
        assertEquals("6x6", version.getGridDefinitions().get(0).getGridSize());
        assertEquals(0, version.getGridDefinitions().get(0).getGridScale());

        assertEquals(3, version.getTranslatedTitles().size());
        assertEquals("väriorto", version.getTranslatedTitles().get("fi"));
        assertEquals("orto i färg", version.getTranslatedTitles().get("sv"));
        assertEquals("ortho in colour", version.getTranslatedTitles().get("en"));
        assertFalse(version.isSingleFile());

        session.logout();

    }

    // @Test
    public void testSaveSingleFileDatasetVersion() throws LoginException,
            RepositoryException {
        Dataset dataset = getTestDataset();
        Session session = repository.login();
        dataset.setPublished(false);
        dao.saveDataset(dataset, session);

        DatasetVersion version = new DatasetVersion();
        version.setName("orto");

        DatasetGridDefinition gridDef = new DatasetGridDefinition();
        gridDef.setCrs("etrs-tm35fin");
        gridDef.setGridSize("None");
        version.getGridDefinitions().add(gridDef);
        dao.saveDatasetVersion(dataset.getFileIdentifier(), version, session);

        session.save();

        version = dao.getDatasetVersion(dataset.getFileIdentifier(), "orto",
                session);
        assertNotNull(version.getDataset());
        assertEquals(dataset.getFileIdentifier(), version.getDataset()
                .getFileIdentifier());
        assertEquals("orto", version.getName());
        assertEquals(1, version.getGridDefinitions().size());
        assertEquals("etrs-tm35fin", version.getGridDefinitions().get(0).getCrs());
        assertNull(version.getGridDefinitions().get(0).getGridSize());
        assertEquals(0, version.getGridDefinitions().get(0).getGridScale());

        assertTrue(version.isSingleFile());

        session.logout();

    }

    @Test
    public void testQueryPublishedDatasets() throws LoginException,
            RepositoryException {

        Dataset dataset = getTestDataset();
        Session session = repository.login(new Credentials() {
        });
        dataset.setPublished(true);

        List<Dataset> datasets = dao.getDatasets(true, session);

        assertTrue(datasets.isEmpty());

        dao.saveDataset(dataset, session);
        session.save();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        datasets = dao.getDatasets(false, session);

        assertEquals(1, datasets.size());
        dataset = datasets.get(0);
        assertEquals("b20a360b-1734-41e5-a5b8-0e90dd9f2af3",
                dataset.getFileIdentifier());

    }

    /*
     * @Test public void testExport() throws LoginException,
     * RepositoryException, IOException { Session session = repository.login(new
     * ExternalAuthenticationCredentials("system", null));
     * 
     * ByteArrayOutputStream bos = new ByteArrayOutputStream();
     * 
     * session.exportSystemView("/meta", bos, true, false);
     * 
     * session.importXML( "/", new ByteArrayInputStream(bos.toByteArray()),
     * ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
     * 
     * session.save(); session.logout();
     * 
     * session = repository.login(new
     * ExternalAuthenticationCredentials("system", null)); Node meta =
     * session.getNode("/meta");
     * 
     * 
     * }
     */
}
