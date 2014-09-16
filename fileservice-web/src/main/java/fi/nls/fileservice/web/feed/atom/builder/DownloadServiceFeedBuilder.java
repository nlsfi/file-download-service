package fi.nls.fileservice.web.feed.atom.builder;

import java.util.List;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetGridDefinition;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.web.common.LinkBuilder;
import fi.nls.fileservice.web.feed.atom.Category;
import fi.nls.fileservice.web.feed.atom.Entry;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public class DownloadServiceFeedBuilder extends InspireAtomFeedBuilder {

    protected List<Dataset> datasets;
    protected LinkBuilder builder;
    protected Map<String, String> reverseMimeTypeMappings;

    /**
     * Constructor
     */
    public DownloadServiceFeedBuilder() {

    }

    /**
     * Constructor
     * 
     * @param metadata
     * @param ctx
     * @param datasets
     * @param builder
     * @param crsDefinitions
     */
    public DownloadServiceFeedBuilder(FeedMetadata metadata,
            AtomRequestContext ctx, List<Dataset> datasets,
            LinkBuilder builder, Map<String, CrsDefinition> crsDefinitions) {
        super(metadata, ctx, crsDefinitions);
        this.datasets = datasets;
        this.builder = builder;
    }

    public void setReverseMimeTypeMappings(
            Map<String, String> reverseMimeTypeMappings) {
        this.reverseMimeTypeMappings = reverseMimeTypeMappings;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public void setLinkBuilder(LinkBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void applyFeedMetadata(Feed feed) {
        feed.setTitle(requestContext
                .getMessage("inspire_atom_feed_title", null));
        feed.setSubtitle(requestContext.getMessage(
                "inspire_atom_feed_subtitle", null));
        feed.setRights(requestContext.getMessage("inspire_atom_feed_rights",
                null));

        Link serviceMetadataLink = new Link();
        UriComponentsBuilder metadataUriComponentsBuilder = UriComponentsBuilder
                .fromUriString(feedMetadata.getMetadataUri());
        serviceMetadataLink.setHref(metadataUriComponentsBuilder
                .buildAndExpand(
                        feedMetadata.getServiceDescriptionFileIdentifier())
                .toUriString());
        serviceMetadataLink.setRel(FeedConstants.LINK_REL_DESCRIBEDBY);
        serviceMetadataLink.setType(feedMetadata.getMetadataMimeType());
        feed.getLinks().add(serviceMetadataLink);

        /*
         * TODO TOTEUTUS ODOTTAA JHS180 valmistumista Link openSearchLink = new
         * Link(); openSearchLink.setHref(
         * "http://lapake01.nls.fi/tp/feed/inspire/opensearch");
         * openSearchLink.setRel("search");
         * openSearchLink.setType("application/opensearchdescription+xml");
         * feed.getLinks().add(openSearchLink);
         */

    }

    @Override
    public void buildEntries(Feed feed) {
        for (Dataset dataset : datasets) {
            List<DatasetVersion> datasetVersions = dataset.getVersions();
            buildDatasetEntries(feed, datasetVersions);
        }
    }

    protected void buildDatasetEntries(Feed feed,
            List<DatasetVersion> datasetVersions) {
        for (DatasetVersion datasetVersion : datasetVersions) {
            Dataset dataset = datasetVersion.getDataset();

            Entry entry = new Entry();

            if (dataset.getSpatialDatasetIdentifierCode() != null) {
                // only export namespace if code is known
                entry.setSpatialDatasetIdentifierCode(dataset
                        .getSpatialDatasetIdentifierCode());
                entry.setSpatialDatasetIdentifierNamespace(dataset
                        .getSpatialDatasetIdentifierNamespace());
            }

            StringBuilder titleBuilder = new StringBuilder(dataset
                    .getTranslatedTitles().get(requestContext.getLanguage()));
            titleBuilder.append(", ");
            titleBuilder.append(datasetVersion.getTranslatedTitles().get(
                    requestContext.getLanguage()));
            entry.setTitle(titleBuilder.toString());

            if (datasetVersion.getLastModified() != null) {
                entry.setUpdated(datasetVersion.getLastModified().getTime());
            }

            UriComponentsBuilder idBuilder = UriComponentsBuilder
                    .fromUri(requestContext.getUriComponentsBuilder()
                            .replaceQuery(null).build().toUri());
            idBuilder.pathSegment(dataset.getName());
            idBuilder.pathSegment(datasetVersion.getName());
            entry.setId(idBuilder.build().toUri());

            Link datasetLink = new Link();
            datasetLink.setHref(builder.buildUri("/" + dataset.getName() + "/"
                    + datasetVersion.getName()));
            datasetLink.setRel(FeedConstants.LINK_REL_ALTERNATE);
            // TODO lang parametri !!!!!

            // IE 8 doesn't show link if type attribute's value is
            // 'application/atom+xml'..
            datasetLink.setType(FeedConstants.ATOM_MIME_TYPE);
            datasetLink.setHreflang(requestContext.getLanguage());
            entry.getLinks().add(datasetLink);

            if (dataset.getFileIdentifier() != null) {
                Link metadataLink = new Link();
                UriComponentsBuilder metadataUriComponentsBuilder = UriComponentsBuilder
                        .fromUriString(feedMetadata.getMetadataUri());
                metadataLink.setHref(metadataUriComponentsBuilder
                        .buildAndExpand(dataset.getFileIdentifier())
                        .toUriString());
                metadataLink.setRel(FeedConstants.LINK_REL_DESCRIBEDBY);
                metadataLink.setType(feedMetadata.getMetadataMimeType());
                entry.getLinks().add(metadataLink);
            }

            List<DatasetGridDefinition> dgs = datasetVersion.getGridDefs();
            for (DatasetGridDefinition dgd : dgs) {
                Category category = getCrsCategory(dgd.getCrs());
                if (category != null) {
                    entry.getCategories().add(category);
                }
            }

            if (reverseMimeTypeMappings != null) {
                // this is only called from mtp feed
                List<String> formats = datasetVersion.getFormats();
                for (String format : formats) {
                    if (reverseMimeTypeMappings.containsKey(format)) {
                        entry.getDistributionFormats().add(
                                reverseMimeTypeMappings.get(format));
                    }
                }
            }

            feed.getEntries().add(entry);
        }
    }

}
