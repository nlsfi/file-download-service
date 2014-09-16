package fi.nls.fileservice.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import fi.nls.fileservice.jcr.repository.JavascriptMetadataServiceExecutor;

//import fi.nls.fileservice.jcr.repository.TestRepositoryProvider;

public class JavascriptEngineProcessorTest {

    // private static TestRepositoryProvider provider;
    private static JavascriptMetadataServiceExecutor executor;

    // private static Repository repository;

    @BeforeClass
    public static void setUp() throws IOException, SAXException,
            RepositoryException {
        executor = new JavascriptMetadataServiceExecutor();
        executor.setScriptProvider(new ClassPathScriptProvider());
        executor.init();
        /*
         * provider = new TestRepositoryProvider(); repository =
         * provider.getRepositories();
         */

    }

    /*
     * @AfterClass public static void tearDown() { provider.shutdown(); }
     */

    @Test
    public void doTestOrtophoto() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/orto/etrs-tm35fin/mara_v_25000_50/2009/L24/02m/1/L4322D.jp2");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("02m", properties.get(MetadataProperty.NLS_ELEVATIONMODEL));
        assertEquals("1", properties.get(MetadataProperty.NLS_FILEVERSION));
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("orto", properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("ortokuva",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("L4322D", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("b20a360b-1734-41e5-a5b8-0e90dd9f2af3",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        // assertEquals("L4322D.tfw",
        // properties.get(MetadataProperty.NLS_RELATED));
        assertEquals("JPEG2000",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("2009",
                properties.get(MetadataProperty.NLS_YEAROFPHOTOGRAPHY));
        assertEquals("orto/etrs-tm35fin/mara_v_25000_50/2009/L24/02m/1/L4322D",
                properties.get(MetadataProperty.NLS_ORTHOPHOTOID));
        assertEquals(10, properties.size());
    }

    @Test
    public void doTestNonExistingPath() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn("/nonexisting/path/and/file.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertTrue(properties.isEmpty());
    }

    @Test
    public void doTestKuntajakoPng() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/kuntajako/kuntajako_1000k/etrs89/png/KarttakuvaaKuntajaosta_2012_1000k.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("kuntajako", properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kuntajako_1000k",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("2012", properties.get(MetadataProperty.NLS_YEAR));
        assertEquals("da40f862-44b5-47b9-aea8-83bb1e640ca9",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestKuntajakoGml() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/kuntajako/kuntajako_1000k/etrs89/gml/TietoaKuntajaosta_2012_1000k.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("kuntajako", properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kuntajako_1000k",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("2012", properties.get(MetadataProperty.NLS_YEAR));
        assertEquals("da40f862-44b5-47b9-aea8-83bb1e640ca9",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestMaastotietokantaShp() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastotietokanta/kaikki/etrs89/shp/K2/K23/K2344L.shp.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastotietokanta",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2344L", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cfe54093-aa87-46e2-bfa2-a20def7b036f",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("ESRI shape",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestMaastotietokantaGml() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastotietokanta/kaikki/etrs89/gml/P6/P61/P6111A1_mtk.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastotietokanta",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("P6111A1", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cfe54093-aa87-46e2-bfa2-a20def7b036f",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestMaastotietokantaKokonaisetKohteet()
            throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastotietokanta/kokonaiset_kohteet/etrs89/gml/K2/K23/K2344R_mtk.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastotietokanta",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kokonaiset_kohteet",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2344R", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cfe54093-aa87-46e2-bfa2-a20def7b036f",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestMaastotietokannanTiestoOsoitteilla()
            throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastotietokanta/tiesto_osoitteilla/etrs89/gml/K32.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastotietokanta",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("tiesto_osoitteilla",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K32", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cfe54093-aa87-46e2-bfa2-a20def7b036f",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestMaastotietokannanTiestoOsoitteillaMif()
            throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastotietokanta/tiesto_osoitteilla/etrs89/mif/K32.zip");

        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastotietokanta",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("tiesto_osoitteilla",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K32", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cfe54093-aa87-46e2-bfa2-a20def7b036f",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("MapInfo mif",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestTaustakarttasarjaKokoSuomi() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/taustakarttasarja/taustakartta_800/100m/etrs89/png/800000.png");
        when(node.getName()).thenReturn("800000.png");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("taustakarttasarja",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("taustakartta_800",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("c22da116-5095-4878-bb04-dd7db3a1a341",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, results.length);
        assertEquals("800000.pgw", results[0]);
        assertEquals(6, properties.size());
        assertFalse(properties.containsKey(MetadataProperty.NLS_GRIDCELL));
    }

    @Test
    public void doTestTaustakarttasarja80k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/taustakarttasarja/taustakartta_10/1m/etrs89/png/UK2L.png");
        when(node.getName()).thenReturn("UK2L.png");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("taustakarttasarja",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("taustakartta_10",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2L", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("c22da116-5095-4878-bb04-dd7db3a1a341",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, results.length);
        assertEquals("UK2L.pgw", results[0]);
        assertEquals(7, properties.size());
    }

    @Test
    public void doTestTaustakarttasarja() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/taustakarttasarja/taustakartta_10/1m/etrs89/png/K2/K23/UK2344R.png");
        when(node.getName()).thenReturn("UK2344R.png");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("taustakarttasarja",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("taustakartta_10",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2344R", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("c22da116-5095-4878-bb04-dd7db3a1a341",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, results.length);
        assertEquals("UK2344R.pgw", results[0]);
        assertEquals(7, properties.size());
    }

    @Test
    public void doTestTaustakarttasarjaPGWNoop() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/taustakarttasarja/taustakartta_10/1m/etrs89/png/K2/K23/UK2344R.pgw");
        when(node.getName()).thenReturn("UK2344R.pgw");
        Map<String, Object> properties = executor.processNode(node);
        assertTrue(properties.isEmpty());
    }

    @Test
    public void doTestMaastokarttarasteri() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastokarttarasteri/painovari/2_5m/etrs89/tiff/K2/K23/UK234_RVK_24.tif");
        when(node.getName()).thenReturn("UK234_RVK_24.tif");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastokarttarasteri",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("painovari",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K234", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("d47ac165-6abd-4357-a4f9-a6f17e2b0c58",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("TIFF",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(2, results.length);
        assertEquals("UK234_RVK_24.tab", results[0]);
        assertEquals("UK234_RVK_24.tfw", results[1]);
        assertEquals(7, properties.size());
    }

    @Test
    public void doTestPeruskarttarasteri() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/peruskarttarasteri/painovari/1m/etrs89/tiff/K2/K24/UK2433L_RVK_1.tif");
        when(node.getName()).thenReturn("UK2433L_RVK_1.tif");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("peruskarttarasteri",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("painovari",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2433L", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("c6e94f34-4925-4fa6-bac9-6b25f4e7cebf",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("TIFF",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(2, results.length);
        assertEquals("UK2433L_RVK_1.tab", results[0]);
        assertEquals("UK2433L_RVK_1.tfw", results[1]);
        assertEquals(7, properties.size());
    }

    @Test
    public void doTestNimistoKarttanimet() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/nimisto/karttanimet_100/etrs89/gml/karttanimet_100_2012_02.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("nimisto", properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("karttanimet_100",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("2012", properties.get(MetadataProperty.NLS_YEAR));
        assertEquals("eec8a276-a406-4b0a-8896-741cd716ade6",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestNimistoPaikat() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/nimisto/paikat/etrs89/gml/paikat_2012_02.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("nimisto", properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("paikat",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("2012", properties.get(MetadataProperty.NLS_YEAR));
        assertEquals("eec8a276-a406-4b0a-8896-741cd716ade6",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("XML/GML",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    @Test
    public void doTestYleiskartta_1000k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/yleiskartta_1000k/kaikki/etrs89/shape/1_milj_Shape_etrs_shape.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("yleiskartta_1000k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("980fa404-75d3-4afd-b97e-bf1a9e392cd9",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("ESRI shape",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(5, properties.size());
    }

    @Test
    public void doTestYleiskartta_4500k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/yleiskartta_4500k/kaikki/etrs89/mif/4_5_milj_MapInfo_etrs_mif.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("yleiskartta_4500k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("95175ec9-0f91-42ca-abca-f4f4359490d3",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("MapInfo mif",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(5, properties.size());
    }

    @Test
    public void doTestYleiskarttarasteri_1000k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/yleiskarttarasteri_1000k/kaikki/etrs89/png/Yleiskarttarasteri_1milj.png");
        when(node.getName()).thenReturn("Yleiskarttarasteri_1milj.png");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("yleiskarttarasteri_1000k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("980fa404-75d3-4afd-b97e-bf1a9e392cd9",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    /*
     * @Test public void doTestYleiskarttarasteri_2000k() throws
     * RepositoryException { Node node = mock(javax.jcr.Node.class);
     * when(node.getPath()).thenReturn(
     * "/tuotteet/yleiskarttarasteri_2000k/kaikki/etrs89/png/Yleiskarttarasteri_2milj.png"
     * ); when(node.getName()).thenReturn("Yleiskarttarasteri_2milj.png");
     * Map<String,Object> properties = executor.processNode(node);
     * assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
     * assertEquals("yleiskarttarasteri_2000k",
     * properties.get(MetadataProperty.NLS_DATASET)); assertEquals("kaikki",
     * properties.get(MetadataProperty.NLS_DATASETVERSION));
     * assertEquals("bb491154-4f95-4b47-b0a3-cf9e1a0a78cc",
     * properties.get(MetadataProperty.GMD_FILEIDENTIFIER)); assertEquals("PNG",
     * properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT)); assertEquals(6,
     * properties.size()); }
     */

    @Test
    public void doTestYleiskarttarasteri_4500k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/yleiskarttarasteri_4500k/kaikki/etrs89/png/Yleiskarttarasteri_45milj.png");
        when(node.getName()).thenReturn("Yleiskarttarasteri_45milj.png");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("yleiskarttarasteri_4500k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("bb491154-4f95-4b47-b0a3-cf9e1a0a78cc",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals(6, properties.size());
    }

    /*
     * @Test public void doTestYleiskarttarasteri_8000k() throws
     * RepositoryException { Node node = mock(javax.jcr.Node.class);
     * when(node.getPath()).thenReturn(
     * "/tuotteet/yleiskarttarasteri_8000k/kaikki/etrs89/png/Yleiskarttarasteri_8milj.png"
     * ); when(node.getName()).thenReturn("Yleiskarttarasteri_8milj.png");
     * Map<String,Object> properties = executor.processNode(node);
     * assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
     * assertEquals("yleiskarttarasteri_8000k",
     * properties.get(MetadataProperty.NLS_DATASET)); assertEquals("kaikki",
     * properties.get(MetadataProperty.NLS_DATASETVERSION));
     * assertEquals("bb491154-4f95-4b47-b0a3-cf9e1a0a78cc",
     * properties.get(MetadataProperty.GMD_FILEIDENTIFIER)); assertEquals("PNG",
     * properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT)); assertEquals(6,
     * properties.size()); }
     */

    @Test
    public void doTestMaastokartta100k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/maastokartta_100k/kaikki/etrs89/shp/K2/K23.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastokartta_100k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K23", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("e9861577-efd5-4448-aded-6131f9d14097",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("ESRI shape",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
    }

    @Test
    public void doTestMaastokartta250k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/maastokartta_250k/kaikki/etrs89/mif/K2/K2R.zip");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastokartta_250k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2R", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("a2cd4d67-ee20-47b7-b899-a4d72e72bb2d",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("MapInfo mif",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
    }

    @Test
    public void doTestMaastokarttarasteri100k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastokarttarasteri_100k/kaikki/etrs89/tiff/K2/K23/UK23R_RVK_5.tif");
        when(node.getName()).thenReturn("UK23R_RVK_5.tif");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastokarttarasteri_100k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K23R", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("5c671d8d-be58-4f5d-8242-a150ecc82f95",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("TIFF",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(2, results.length);
        assertEquals("UK23R_RVK_5.tab", results[0]);
        assertEquals("UK23R_RVK_5.tfw", results[1]);
        assertEquals(7, properties.size());
    }

    @Test
    public void doTestMaastokarttarasteri250k() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/maastokarttarasteri_250k/kaikki/etrs89/tiff/K2/UK2R_RVK.tif");
        when(node.getName()).thenReturn("UK2R_RVK.tif");
        Map<String, Object> properties = executor.processNode(node);
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("maastokarttarasteri_250k",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K2R", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("924a68ba-665f-4ea0-a830-26e80112b5dc",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("TIFF",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        String[] results = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(2, results.length);
        assertEquals("UK2R_RVK.tab", results[0]);
        assertEquals("UK2R_RVK.tfw", results[1]);
        assertEquals(7, properties.size());
    }

    /*
     * @Test public void doTestLaserkeilausaineisto() throws RepositoryException
     * { Session session = repository.login(); Node node = session.getNode(
     * "/tuotteet/laser/etrs-tm35fin-n2000/mara_2m/2011/K344/1/K3441A1.laz");
     * Map<String,Object> properties = executor.processNode(node);
     * 
     * assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
     * assertEquals("2011",
     * properties.get(MetadataProperty.NLS_YEAROFSCANNING)); assertEquals("1",
     * properties.get(MetadataProperty.NLS_FILEVERSION));
     * assertEquals("K3441A1", properties.get(MetadataProperty.NLS_GRIDCELL));
     * assertEquals("0e55977c-00c9-4c46-9c87-dee6b27d2d5c",
     * properties.get(MetadataProperty.GMD_FILEIDENTIFIER)); assertEquals("LAZ",
     * properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
     * assertNull(properties.get(MetadataProperty.NLS_INFO));
     * assertEquals("laser", properties.get(MetadataProperty.NLS_DATASET));
     * assertEquals("etrs-tm35fin-n2000",
     * properties.get(MetadataProperty.NLS_DATASETVERSION));
     * 
     * // String propertyDate = new
     * java.text.SimpleDateFormat("dd.MM.yyyy").format
     * (properties.get(MetadataProperty.NLS_DATEOFSCAN)); //
     * assertEquals("11.05.2008", propertyDate); assertEquals("A/B",
     * properties.get(MetadataProperty.NLS_TIMEFRAME));
     * assertEquals("2 pulssia",
     * properties.get(MetadataProperty.NLS_MULTIPULSE));
     * assertEquals("Leica ALS60",
     * properties.get(MetadataProperty.NLS_SCANNER)); assertEquals("2124",
     * properties.get(MetadataProperty.NLS_FLIGHTALTITUDE));
     * assertEquals("0,15",
     * properties.get(MetadataProperty.NLS_ELEVATIONPRECISION));
     * assertEquals("0,80", properties.get(MetadataProperty.NLS_POINTDENSITY));
     * //assertEquals("N2000",
     * properties.get(MetadataProperty.NLS_ELEVATIONMODEL));
     * assertEquals("20110003_BLOM_hanko",
     * properties.get(MetadataProperty.NLS_PROJECT));
     * assertEquals("laser/etrs-tm35fin-n2000/mara_2m/2011/K344/1/K3441A1",
     * properties.get(MetadataProperty.NLS_POINTCLOUDID)); //assertEquals(15,
     * properties.size()); //TODO
     * 
     * session.logout(); }
     */

    @Test
    public void doTestKorkeusmalli2m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/korkeusmalli_2m/kaikki/etrs89/ascii_grid/R4/R41/R4131.zip");
        Map<String, Object> properties = executor.processNode(node);

        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("R4131", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("dd32d539-a8de-4c4e-aa44-523551ffec99",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("ascii grid",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("korkeusmalli_2m",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
    }

    @Test
    public void doTestKorkeusmalli10m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/korkeusmalli_10m/kaikki/etrs89/ascii_xyz/R4/R41/R4131H.zip");
        Map<String, Object> properties = executor.processNode(node);

        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("R4131H", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cd640425-315f-4b12-86c5-192e98701dcb",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("ascii xyz",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("korkeusmalli_10m",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
    }

    @Test
    public void doTestKorkeusmalli10mHila() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/korkeusmalli_10m/hila_10/etrs89/png/R4/R41/R4131H.png");
        when(node.getName()).thenReturn("R4131H.png");
        Map<String, Object> properties = executor.processNode(node);

        assertEquals(7, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("R4131H", properties.get(MetadataProperty.NLS_GRIDCELL));
        assertEquals("cd640425-315f-4b12-86c5-192e98701dcb",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("korkeusmalli_10m",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_10",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("R4131H.pgw", related[0]);
    }

    @Test
    public void doTestLaserkeilausaineistoCsvNoop() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/laser/etrs-tm35fin-n2000/mara_2m/2011/K344/1/K3442B1.csv");
        Map<String, Object> properties = executor.processNode(node);
        assertTrue(properties.isEmpty());
    }

    /*
     * @Test public void doTestKiinteistorekisterikartta() throws
     * RepositoryException { Node node = mock(javax.jcr.Node.class);
     * when(node.getPath()).thenReturn(
     * "/tuotteet/kiinteistorekisterikartta/kaikki/etrs89/shp/091.zip");
     * 
     * Map<String,Object> properties = executor.processNode(node);
     * 
     * assertEquals(5, properties.size()); assertEquals("etrs-tm35fin",
     * properties.get(MetadataProperty.NLS_CRS));
     * assertEquals("472b3e52-5ba8-4967-8785-4fa13955b42e",
     * properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
     * assertEquals("ESRI shape",
     * properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
     * assertEquals("kiinteistorekisterikartta",
     * properties.get(MetadataProperty.NLS_DATASET)); assertEquals("kaikki",
     * properties.get(MetadataProperty.NLS_DATASETVERSION)); }
     */

    @Test
    public void doTestVinovalovarjoste() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/vinovalovarjoste/hila_10m/etrs89/png/Q3/Q33/Q3311S010.png");
        when(node.getName()).thenReturn("Q3311S010.png");

        Map<String, Object> properties = executor.processNode(node);

        assertEquals(7, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("1f247c72-4487-4d20-9595-985560343066",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("vinovalovarjoste",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_10m",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("Q3311", properties.get(MetadataProperty.NLS_GRIDCELL));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("Q3311S010.pgw", related[0]);
    }

    @Test
    public void doTestVinovalovarjosteHila40m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/vinovalovarjoste/hila_40m/etrs89/png/K23S040.png");
        when(node.getName()).thenReturn("K23S040.png");

        Map<String, Object> properties = executor.processNode(node);

        assertEquals(7, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("1f247c72-4487-4d20-9595-985560343066",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("vinovalovarjoste",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_40m",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K23", properties.get(MetadataProperty.NLS_GRIDCELL));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("K23S040.pgw", related[0]);
    }

    @Test
    public void doTestVinovalovarjoste_640m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/vinovalovarjoste/hila_640m/etrs89/png/KM10_S640.png");
        when(node.getName()).thenReturn("KM10_S640.png");

        Map<String, Object> properties = executor.processNode(node);

        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("1f247c72-4487-4d20-9595-985560343066",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("vinovalovarjoste",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_640m",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("KM10_S640.pgw", related[0]);
    }

    @Test
    public void doTestKorkeusvyohyke_40m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/korkeusvyohyke/hila_40m/etrs89/png/K24Z040.png");
        when(node.getName()).thenReturn("K24Z040.png");

        Map<String, Object> properties = executor.processNode(node);

        assertEquals(7, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("5c2ba253-e1b0-42c8-b9bb-3bac947e1cf1",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("korkeusvyohyke",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_40m",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        assertEquals("K24", properties.get(MetadataProperty.NLS_GRIDCELL));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("K24Z040.pgw", related[0]);
    }

    @Test
    public void doTestKorkeusvyohyke_640m() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath()).thenReturn(
                "/tuotteet/korkeusvyohyke/hila_640m/etrs89/png/KM10_Z640.png");
        when(node.getName()).thenReturn("KM10_Z640.png");

        Map<String, Object> properties = executor.processNode(node);

        assertEquals(6, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("5c2ba253-e1b0-42c8-b9bb-3bac947e1cf1",
                properties.get(MetadataProperty.GMD_FILEIDENTIFIER));
        assertEquals("PNG",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("korkeusvyohyke",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("hila_640m",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
        String[] related = (String[]) properties
                .get(MetadataProperty.NLS_RELATED);
        assertEquals(1, related.length);
        assertEquals("KM10_Z640.pgw", related[0]);
    }

    @Test
    public void doTestKarttalehtijako() throws RepositoryException {
        Node node = mock(javax.jcr.Node.class);
        when(node.getPath())
                .thenReturn(
                        "/tuotteet/karttalehtijako_ruudukko/kaikki/etrs89/shp/UTM_EUREF_SHP.zip");
        Map<String, Object> properties = executor.processNode(node);

        assertEquals(4, properties.size());
        assertEquals("etrs-tm35fin", properties.get(MetadataProperty.NLS_CRS));
        assertEquals("ESRI shape",
                properties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
        assertEquals("karttalehtijako_ruudukko",
                properties.get(MetadataProperty.NLS_DATASET));
        assertEquals("kaikki",
                properties.get(MetadataProperty.NLS_DATASETVERSION));
    }

}
