package fi.nls.fileservice.web.feed.atom.builder;

import java.util.Date;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.web.feed.atom.Author;
import fi.nls.fileservice.web.feed.atom.Category;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public abstract class InspireAtomFeedBuilder implements FeedBuilder {

    protected FeedMetadata feedMetadata;
    protected Map<String, CrsDefinition> crsDefinitions;
    protected AtomRequestContext requestContext;

    /**
     * Constructor
     */
    public InspireAtomFeedBuilder() {
    }

    /**
     * Constructor
     * 
     * @param feedMetadata
     * @param requestContext
     * @param crsDefinitons
     */
    public InspireAtomFeedBuilder(FeedMetadata feedMetadata,
            AtomRequestContext requestContext,
            Map<String, CrsDefinition> crsDefinitons) {
        this.feedMetadata = feedMetadata;
        this.requestContext = requestContext;
        this.crsDefinitions = crsDefinitons;
    }

    public void setFeedMetadata(FeedMetadata feedMetadata) {
        this.feedMetadata = feedMetadata;
    }

    public void setAtomRequestContext(AtomRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public void setCrsDefinitions(Map<String, CrsDefinition> crsDefinitions) {
        this.crsDefinitions = crsDefinitions;
    }

    /**
     * Converts srs identifier to INSPIRE required format (atom:category)
     * 
     * @param crs
     *            srs identifier
     * @return atom:category
     */
    protected Category getCrsCategory(String crs) {
        CrsDefinition def = crsDefinitions.get(crs);
        Category crsCategory = null;
        if (def != null) {
            crsCategory = new Category();
            crsCategory.setTerm(def.getInspireUri());
            crsCategory.setLabel(def.getInspireLabel());
        }
        return crsCategory;
    }

    @Override
    public Feed getFeed() {

        String currentLanguage = requestContext.getLanguage();

        Feed feed = new Feed();

        // create a copy of uri components to remove query string for atom:id
        UriComponentsBuilder idBuilder = UriComponentsBuilder
                .fromUri(requestContext.getUriComponentsBuilder().build()
                        .toUri());
        feed.setId(idBuilder.replaceQuery(null).build().toUri());

        feed.setLang(currentLanguage);
        feed.setUpdated(new Date());

        Author author = new Author();
        author.setName(requestContext.getMessage(
                "inspire_atom_feed_author_name", null));
        author.setEmail(requestContext.getMessage(
                "inspire_atom_feed_author_email", null));
        feed.getAuthors().add(author);

        UriComponentsBuilder uriCompBuilder = requestContext
                .getUriComponentsBuilder();

        Link self = new Link();
        self.setHref(uriCompBuilder.build().toUriString());
        self.setRel(FeedConstants.LINK_REL_SELF);
        self.setType(FeedConstants.ATOM_MIME_TYPE);
        self.setHreflang(currentLanguage);
        self.setTitle(requestContext.getMessage("inspire_atom_feed_self_title",
                null));
        feed.getLinks().add(self);

        if (feedMetadata.getLanguages() != null) {
            for (String altLanguage : feedMetadata.getLanguages()) {
                if (!altLanguage.equals(currentLanguage)) {
                    uriCompBuilder.replaceQueryParam("lang", altLanguage);
                    Link altLanguageLink = new Link();
                    altLanguageLink.setHref(uriCompBuilder.build()
                            .toUriString());
                    altLanguageLink.setRel(FeedConstants.LINK_REL_ALTERNATE);
                    altLanguageLink.setType(FeedConstants.ATOM_MIME_TYPE);
                    altLanguageLink.setHreflang(altLanguage);
                    feed.getLinks().add(altLanguageLink);
                }
            }
        }

        applyFeedMetadata(feed);

        buildEntries(feed);

        return feed;

    }

    public abstract void applyFeedMetadata(Feed feed);

    public abstract void buildEntries(Feed feed);

}
