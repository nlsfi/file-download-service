package fi.nls.fileservice.web.feed.atom.builder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetGridDefinition;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.DetachedProperty;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.web.common.LinkBuilder;
import fi.nls.fileservice.web.feed.atom.Feed;

public class InspireAtomFeedBuilderTest {

    private static FeedMetadata feedMetadata;
    private static List<Dataset> datasetList;
    // private static Dataset dataset;
    // private static FileServiceQueryImpl fsqi;
    private static AtomRequestContext context;
    private static Map<String, CrsDefinition> crsDefinitions;

    @BeforeClass
    public static void doSetUp() {

        feedMetadata = new FeedMetadata();
        feedMetadata
                .setMetadataUri("http://www.paikkatietohakemisto.fi/geonetwork/srv/en/csw?service=CSW&amp;request=GetRecordById&amp;ID={fileIdentifier}&amp;outputSchema=csw:IsoRecord");

        Dataset d1 = new Dataset();
        d1.getTranslatedTitles().put("fi", "Maanmittauslaitoksen ortokuva");
        d1.setFileIdentifier("b20a360b-1734-41e5-a5b8-0e90dd9f2af3");

        Dataset d2 = new Dataset();
        d2.getTranslatedTitles().put("fi", "Kuntajako");
        d2.setFileIdentifier("da40f862-44b5-47b9-aea8-83bb1e640ca9");

        DatasetGridDefinition dgd = new DatasetGridDefinition();
        dgd.setCrs("EPSG:3067");

        DatasetVersion v1 = new DatasetVersion();
        v1.getTranslatedTitles().put("fi", "Kuntajako 1:250 000");
        v1.setSingleFile(true);
        v1.setDataset(d2);
        v1.setName("kuntajako_250k");
        v1.getGridDefinitions().add(dgd);

        // v1.setGridDefs(gridDefs)

        // dataset = d2;
        d2.getVersions().add(v1);

        datasetList = new ArrayList<Dataset>(2);
        datasetList.add(d1);
        datasetList.add(d2);

        List<DetachedNode> files = new ArrayList<DetachedNode>(1);
        DetachedNode file = new DetachedNode();
        file.setName("Kuntajako_2012_250k_gml.zip");
        file.setPath("/aineistot/kuntajako/2012/" + file.getName());
        file.setLength(1538968);
        file.setMimeType("application/zip");

        DetachedProperty format = new DetachedProperty();
        format.setName(MetadataProperty.GMD_DISTRIBUTIONFORMAT);
        format.setValue("XML/GML");
        file.getProperties().add(format);
        files.add(file);

        /*
         * DatasetFile file2 = new DatasetFile();
         * file2.setName("Kuntajako_2012_10k_gml.zip");
         * file2.setPath("/aineistot/kuntajako/2012/" + file.getName());
         * file2.setLength(31976596); file2.setMimeType("application/zip");
         * file2.setDistributionFormat("XML/GML"); files.add(file);
         */

        // fsqi = new FileServiceQueryImpl(files);

        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("messages");
        ms.setDefaultEncoding("iso-8859-1");
        ms.setCacheSeconds(0);

        context = new AtomRequestContext();
        context.setMessageSource(ms);
        context.setLocale(new Locale("fi", "FI"));
        context.setUriComponents(UriComponentsBuilder.fromHttpUrl(
                "https://data.nls.fi/feed/inspire").build());

        // TODO get this from spring application context
        CrsDefinition crs = new CrsDefinition();
        crs.setInspireUri("http://www.opengis.net/def/crs/EPSG/0/3067");
        crs.setInspireLabel("ETRS89 / ETRS-TM35FIN");
        crs.setCrsId("etrs-tm35fin");
        crs.setEpsgId("EPSG:3067");
        crsDefinitions = new HashMap<String, CrsDefinition>();
        crsDefinitions.put(crs.getCrsId(), crs);

    }

    @Test
    public void doTestDatasetListFeed() throws JAXBException {

        DownloadServiceFeedBuilder builder = new DownloadServiceFeedBuilder(
                feedMetadata, context, datasetList, new LinkBuilder() {

                    @Override
                    public String buildUri(String path) {
                        return "http://test.example.com/" + path;
                    }

                }, crsDefinitions);

        Feed feed = builder.getFeed();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JAXBContext context = JAXBContext.newInstance(Feed.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(feed, bos);

        // todo !!! actually validate !!

    }

    /*
     * @Test public void doTestDatasetFeed() throws JAXBException, SAXException,
     * IOException {
     * 
     * //InspireAtomFeedBuilder builder = new DatasetFeedBuilder(feedMetadata,
     * context, dataset.getVersions().get(0), "etrs-tm35fin", fsqi, //
     * crsDefinitions); //Feed feed = builder.getFeed();
     * 
     * SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.XML_NS_URI);
     * 
     * InputStream in =
     * InspireAtomFeedBuilderTest.class.getClassLoader().getResourceAsStream
     * ("atom.xsd"); StreamSource src = new StreamSource(in);
     * 
     * Schema schema = sf.newSchema(src);
     * 
     * JAXBContext context = JAXBContext.newInstance(Feed.class); Marshaller
     * marshaller = context.createMarshaller(); marshaller.setSchema(schema);
     * 
     * // schema validation doesn't work //marshaller.marshal(feed, new
     * DefaultHandler());
     * 
     * //if (in != null) { //in.close(); //}
     * 
     * 
     * }
     */

}
