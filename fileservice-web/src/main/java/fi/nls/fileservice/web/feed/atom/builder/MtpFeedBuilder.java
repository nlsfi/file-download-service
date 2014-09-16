package fi.nls.fileservice.web.feed.atom.builder;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.util.AbstractRowIteratorWrapper;
import fi.nls.fileservice.util.LazyQueryResultList;
import fi.nls.fileservice.web.common.LinkBuilder;
import fi.nls.fileservice.web.feed.atom.Author;
import fi.nls.fileservice.web.feed.atom.Entry;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public class MtpFeedBuilder implements FeedBuilder {

    private DatasetVersion datasetVersion;
    private FileService service;
    private DatasetQueryParams queryParams;
    private AtomRequestContext requestContext;
    private LinkBuilder linkBuilder;
    private int maxResultsPerPage;

    public MtpFeedBuilder(DatasetVersion datasetVersion, FileService service,
            DatasetQueryParams queryParams, AtomRequestContext ctx,
            LinkBuilder linkBuilder, int maxResultsPerPage) {
        this.datasetVersion = datasetVersion;
        this.service = service;
        this.queryParams = queryParams;
        this.requestContext = ctx;
        this.linkBuilder = linkBuilder;
        this.maxResultsPerPage = maxResultsPerPage;
    }

    public void applyFeedMetadata(Feed feed) {

        feed.setTitle(requestContext.getMessage(
                "mtp_dataset_feed_title",
                new String[] {
                        datasetVersion.getDataset().getTranslatedTitles()
                                .get(requestContext.getLanguage()),
                        datasetVersion.getTranslatedTitles().get(
                                requestContext.getLanguage()) }));

        if (datasetVersion.getDataset().getLicence() == Licence.OPENDATA) {
            Link licenseLink = new Link();
            licenseLink.setHref(requestContext.getMessage(
                    "opendata_license_url", null));
            licenseLink.setTitle(requestContext.getMessage("opendata_license",
                    null));
            licenseLink.setType("text/html");
            licenseLink.setRel(FeedConstants.LINK_REL_LICENSE);
            feed.getLinks().add(licenseLink);
        }

        Author author = new Author();
        author.setName(requestContext.getMessage(
                "inspire_atom_feed_author_name", null));
        author.setEmail(requestContext.getMessage(
                "inspire_atom_feed_author_email", null));
        feed.getAuthors().add(author);

        if (datasetVersion.getLastModified() != null) {
            feed.setUpdated(datasetVersion.getLastModified().getTime());
        }
    }

    public void buildEntries(Feed feed) {

        RowIterator rowIterator = service.queryRows(queryParams);

        long resultsSize = rowIterator.getSize();
        if (resultsSize >= maxResultsPerPage) { // ||
                                                // queryParams.isOffsetQuery())
                                                // {
            Link nextLink = new Link();
            nextLink.setRel("next");
            nextLink.setType(FeedConstants.ATOM_MIME_TYPE);
            nextLink.setHref(requestContext.getUriComponentsBuilder()
                    .queryParam("offset", queryParams.getNextOffset()).build()
                    .toUriString());
            feed.getLinks().add(nextLink);
        }

        // build a lazy result list that enables streaming of the updating
        // service feed
        // that can take a long time (minutes in worst cases) to build otherwise
        // files are queried from disk and entries build as the feed is streamed
        // to client
        LazyQueryResultList<Entry> entries = new LazyQueryResultList<Entry>(
                new AbstractRowIteratorWrapper<Entry>(rowIterator) {

                    @Override
                    public Entry next() {
                        try {

                            Row row = super.rowIterator.nextRow();
                            Node node = row.getNode();

                            Node content = node.getNode(Node.JCR_CONTENT);

                            Entry entry = new Entry();
                            entry.setTitle(node.getName());
                            entry.setId("urn:path:" + node.getIdentifier());

                            if (node.hasProperty(MetadataProperty.NLS_FILECHANGED)) {
                                entry.setUpdated(node
                                        .getProperty(
                                                MetadataProperty.NLS_FILECHANGED)
                                        .getDate().getTime());
                            } else {
                                entry.setUpdated(content
                                        .getProperty(Property.JCR_LAST_MODIFIED)
                                        .getDate().getTime());
                            }

                            Link downloadLink = new Link();
                            downloadLink
                                    .setRel(FeedConstants.LINK_REL_ALTERNATE);
                            downloadLink.setLength(Long
                                    .toString(content.getProperty(
                                            Property.JCR_DATA).getLength()));
                            downloadLink.setType(content.getProperty(
                                    Property.JCR_MIMETYPE).getString());
                            downloadLink.setHref(linkBuilder.buildUri(node
                                    .getPath()));
                            entry.getLinks().add(downloadLink);

                            // link related files (raster world files) as
                            // link="related"
                            if (node.hasProperty(MetadataProperty.NLS_RELATED)) {
                                String parentPath = node.getParent().getPath();
                                Value[] values = node.getProperty(
                                        MetadataProperty.NLS_RELATED)
                                        .getValues();
                                for (Value v : values) {
                                    // don't output length & type to avoid extra
                                    // disk accesses
                                    // for getting corresponding JCR node
                                    String name = v.getString();
                                    Link relatedLink = new Link();
                                    relatedLink
                                            .setRel(FeedConstants.LINK_REL_RELATED);
                                    relatedLink.setHref(linkBuilder
                                            .buildUri(parentPath + "/" + name));
                                    relatedLink.setTitle(name);
                                    entry.getLinks().add(relatedLink);
                                }
                            }

                            return entry;
                        } catch (RepositoryException e) {
                            throw new DataAccessException(e);
                        }
                    }

                });

        feed.setEntries(entries);
    }

    @Override
    public Feed getFeed() {
        Feed feed = new Feed();
        this.applyFeedMetadata(feed);
        this.buildEntries(feed);
        return feed;
    }

}
