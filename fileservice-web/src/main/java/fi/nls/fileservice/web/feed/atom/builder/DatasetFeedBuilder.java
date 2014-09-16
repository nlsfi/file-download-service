package fi.nls.fileservice.web.feed.atom.builder;

import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetGridDefinition;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.SpatialObjectType;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.util.AbstractRowIteratorWrapper;
import fi.nls.fileservice.util.LazyQueryResultList;
import fi.nls.fileservice.web.feed.atom.Category;
import fi.nls.fileservice.web.feed.atom.Entry;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public class DatasetFeedBuilder extends InspireAtomFeedBuilder {

    protected Dataset dataset;
    protected FileService fileService;
    protected String crs;
    protected DatasetVersion datasetVersion;

    public DatasetFeedBuilder(FeedMetadata feedMetadata,
            AtomRequestContext ctx, DatasetVersion datasetVersion, String crs,
            FileService fileService, Map<String, CrsDefinition> crsDefinitions) {
        super(feedMetadata, ctx, crsDefinitions);
        this.dataset = datasetVersion.getDataset();
        this.datasetVersion = datasetVersion;
        this.crs = crs;
        this.feedMetadata = feedMetadata;
        this.fileService = fileService;
    }

    @Override
    public void applyFeedMetadata(Feed feed) {

        StringBuilder bb = new StringBuilder();
        if (dataset != null) {
            bb.append(dataset.getTranslatedTitles().get(
                    requestContext.getLanguage()));
            bb.append(", ");
        }
        bb.append(datasetVersion.getTranslatedTitles().get(
                requestContext.getLanguage()));

        feed.setTitle(requestContext.getMessage(
                "inspire_atom_feed_dataset_title",
                new Object[] { bb.toString() }));

        Link metadataLink = new Link();

        if (dataset.getFileIdentifier() != null) {
            UriTemplate template = new UriTemplate(
                    feedMetadata.getMetadataUri());
            metadataLink.setHref(template.expand(dataset.getFileIdentifier())
                    .toString());
            metadataLink.setRel(FeedConstants.LINK_REL_DESCRIBEDBY);
            metadataLink.setType(feedMetadata.getMetadataMimeType());
            feed.getLinks().add(metadataLink);
        }

        List<SpatialObjectType> spatialObjectTypes = dataset
                .getSpatialObjectTypes();
        for (SpatialObjectType type : spatialObjectTypes) {
            Link typeLink = new Link();
            typeLink.setHref(type.getUri());
            typeLink.setRel(FeedConstants.LINK_REL_DESCRIBEDBY);
            typeLink.setType("text/html");
            feed.getLinks().add(typeLink);
        }

        Link upLink = new Link();
        upLink.setRel(FeedConstants.LINK_REL_UP);
        upLink.setType(FeedConstants.ATOM_MIME_TYPE);
        upLink.setTitle(requestContext.getMessage("inspire_atom_feed_title",
                null));

        UriComponentsBuilder upLinkBuilder = requestContext
                .getUriComponentsBuilder();
        upLinkBuilder.path("/feed/inspire"); // TODO remove hardcoded path
        upLink.setHref(upLinkBuilder.build().toUriString());
        feed.getLinks().add(upLink);
    }

    protected List<DetachedNode> queryDatasetNodes(
            DatasetVersion datasetVersion, String crs, String format) {
        DatasetQueryParams dq = new DatasetQueryParams();
        dq.setDataset(datasetVersion.getDataset().getName());
        dq.setDatasetVersion(datasetVersion.getName());
        // dq.setPath(datasetVersion.getDataset().getPath());
        dq.setCrs(crs);
        dq.setDistributionFormat(format);

        return fileService.queryNodes(dq, null);
    }

    @Override
    public void buildEntries(Feed feed) {
        List<DatasetGridDefinition> gridDefinitions = datasetVersion
                .getGridDefs();
        List<String> formats = datasetVersion.getFormats();
        for (DatasetGridDefinition gridDef : gridDefinitions) {
            for (String format : formats) {

                DatasetQueryParams dqp = new DatasetQueryParams();
                dqp.setDataset(datasetVersion.getDataset().getName());
                dqp.setDatasetVersion(datasetVersion.getName());
                dqp.setCrs(crs);
                dqp.setDistributionFormat(format);
                dqp.setOrderBy(MetadataProperty.NLS_FILECHANGED);

                RowIterator rowIterator = fileService.queryRows(dqp);
                LazyQueryResultList<Link> results = new LazyQueryResultList<Link>(
                        new InspireFilesIterator(rowIterator, datasetVersion));

                Entry entry = new Entry();

                entry.setLinks(results);

                StringBuilder titleBuilder = new StringBuilder(datasetVersion
                        .getTranslatedTitles()
                        .get(requestContext.getLanguage()));
                titleBuilder.append(", ");
                if (crs != null) {
                    titleBuilder.append(crs);
                    titleBuilder.append(", ");
                }
                titleBuilder.append(format);
                entry.setTitle(titleBuilder.toString());

                if (datasetVersion.getLastModified() != null) {
                    entry.setUpdated(datasetVersion.getLastModified().getTime());
                }

                UriComponentsBuilder idBuilder = UriComponentsBuilder
                        .fromUri(requestContext.getUriComponentsBuilder()
                                .replaceQuery(null).build().toUri());
                idBuilder.pathSegment(dataset.getName());
                idBuilder.pathSegment(datasetVersion.getName());
                idBuilder.pathSegment(format.replaceAll(" ", "_"));
                entry.setId(idBuilder.build().toUri());

                Category category = super.getCrsCategory(gridDef.getCrs());
                if (category != null) {
                    entry.getCategories().add(category);
                }

                feed.getEntries().add(entry);

            }
        }
    }

    class InspireFilesIterator extends AbstractRowIteratorWrapper<Link> {

        protected DatasetVersion datasetVersion;
        protected boolean isSingleFile;
        protected boolean isDescriptionLinkEmitted;

        public InspireFilesIterator(RowIterator rowIterator,
                DatasetVersion datasetVersion) {
            super(rowIterator);
            this.datasetVersion = datasetVersion;
            this.isSingleFile = datasetVersion.isSingleFile();
        }

        @Override
        public boolean hasNext() {
            if (!isSingleFile && !isDescriptionLinkEmitted) {
                return true;
            }
            return rowIterator.hasNext();
        }

        @Override
        public Link next() {
            if (!isSingleFile && !isDescriptionLinkEmitted) {
                isDescriptionLinkEmitted = true;
                Link datasetStructureDescriptionLink = new Link();
                datasetStructureDescriptionLink
                        .setRel(FeedConstants.LINK_REL_ALTERNATE);
                datasetStructureDescriptionLink.setType("text/html");
                datasetStructureDescriptionLink.setHref(requestContext
                        .getMessage("lehtijako_description_url", null));
                datasetStructureDescriptionLink.setTitle(requestContext
                        .getMessage("lehtijako_description_label", null));
                return datasetStructureDescriptionLink;
            } else {
                try {

                    Row row = rowIterator.nextRow();
                    Node node = row.getNode();
                    Node content = node.getNode(Node.JCR_CONTENT);

                    Link link = new Link();
                    if (isSingleFile) {
                        // dataset is contained in a single file, so we just add
                        // a link pointing to that
                        link.setRel(FeedConstants.LINK_REL_ALTERNATE);
                        // TODO
                        // entry.setUpdated(datasetNodes.get(0).getLastModified());
                    } else {
                        // dataset is contained in a single file, so we just add
                        // a link pointing to that
                        link.setRel(FeedConstants.LINK_REL_SECTION);
                    }

                    link.setType(content.getProperty(Property.JCR_MIMETYPE)
                            .getString());
                    link.setLength(Long.toString(content
                            .getProperty(Property.JCR_DATA).getBinary()
                            .getSize()));

                    UriComponents downloadUri = requestContext
                            .getUriComponentsBuilder().path("/lataus")
                            .path(node.getPath()).build();

                    link.setHref(downloadUri.toUriString());
                    ;
                    link.setTitle(node.getName());

                    return link;
                } catch (RepositoryException re) {
                    throw new DataAccessException(re);
                }
            }

        }

    }

}
