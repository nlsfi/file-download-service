package fi.nls.fileservice.web.feed.atom.builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.web.common.LinkBuilder;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public class MtpServiceFeedBuilder extends DownloadServiceFeedBuilder {

    public MtpServiceFeedBuilder() {
        super();
    }

    public MtpServiceFeedBuilder(FeedMetadata metadata, AtomRequestContext ctx,
            List<Dataset> datasets, LinkBuilder builder,
            Map<String, CrsDefinition> crsDefinitions) {
        super(metadata, ctx, datasets, builder, crsDefinitions);
    }

    @Override
    public void applyFeedMetadata(Feed feed) {
        feed.setTitle(requestContext.getMessage("mtp_service_feed_title", null));
        feed.setSubtitle(requestContext.getMessage("mtp_service_feed_subtitle", null));
        feed.setRights(requestContext.getMessage("mtp_opendata_rights", null));

        Link serviceMetadataLink = new Link();
        UriComponentsBuilder metadataUriComponentsBuilder = UriComponentsBuilder
                .fromUriString(feedMetadata.getMetadataUri());
        serviceMetadataLink.setHref(metadataUriComponentsBuilder
                .buildAndExpand(
                        feedMetadata.getServiceDescriptionFileIdentifier()).toUriString());
        serviceMetadataLink.setRel(FeedConstants.LINK_REL_DESCRIBEDBY);
        serviceMetadataLink.setType(feedMetadata.getMetadataMimeType());
        feed.getLinks().add(serviceMetadataLink);
    }

    @Override
    public void buildEntries(Feed feed) {

        // sort datasets in descending order by dataset version's last
        // modification dates
        // this puts the last updated dataset version on top of the feed. ATOM
        // readers like this
        List<DatasetVersion> sortedDatasetVersions = new ArrayList<DatasetVersion>();
        for (Dataset dataset : datasets) {
            for (DatasetVersion version : dataset.getVersions()) {
                sortedDatasetVersions.add(version);
            }
        }

        if (sortedDatasetVersions.size() > 0) {

            // sort datasets by last modification date
            java.util.Collections.sort(sortedDatasetVersions,
                    new Comparator<DatasetVersion>() {

                        @Override
                        public int compare(DatasetVersion dv1, DatasetVersion dv2) {
                            if (dv2.getLastModified() != null) {
                                if (dv1.getLastModified() != null) {
                                    return dv2.getLastModified().compareTo(dv1.getLastModified());
                                }
                                return 1;
                            } else if (dv1.getLastModified() != null) {
                                return -1;
                            }
                            return 0;
                        }
                    });

            Calendar lastUpdatedDataset = sortedDatasetVersions.get(0).getLastModified();
            if (lastUpdatedDataset != null) {
                feed.setUpdated(lastUpdatedDataset.getTime());
            }

            super.buildDatasetEntries(feed, sortedDatasetVersions);
        }
    }

}
