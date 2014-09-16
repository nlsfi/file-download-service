package fi.nls.fileservice.web.feed.atom.builder;

import java.util.List;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.web.feed.atom.Entry;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.Link;

public class DirectoryFeedBuilder {

    private DetachedNode detachedNode;
    private UriComponents baseUriComponents;

    public DirectoryFeedBuilder(DetachedNode folder,
            UriComponentsBuilder uriComponentsBuilder) {
        this.detachedNode = folder;
        this.baseUriComponents = uriComponentsBuilder.build();
    }

    public Feed getFeed() {
        Feed feed = new Feed();
        feed.setTitle(detachedNode.getName());
        feed.setUpdated(detachedNode.getLastModified());

        UriComponents feedUriComponents = UriComponentsBuilder
                .fromHttpUrl(baseUriComponents.toUriString())
                .path(detachedNode.getPath()).build();

        feed.setId(feedUriComponents.toUri());

        List<DetachedNode> childNodes = detachedNode.getChildNodes();
        for (DetachedNode node : childNodes) {
            Entry entry = new Entry();
            entry.setTitle(node.getName());
            entry.setUpdated(node.getLastModified());

            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(baseUriComponents.toUriString())
                    .path(node.getPath()).build();

            entry.setId(uriComponents.toUri());

            Link link = new Link();
            link.setHref(uriComponents.toUriString());
            if (node.isFolder()) {
                link.setRel("collection");
                link.setType("application/atom+xml");
            } else {
                link.setRel("enclosure");
                link.setLength(Long.toString(node.getLength()));
                link.setType(node.getMimeType());
            }
            entry.getLinks().add(link);

            feed.getEntries().add(entry);
        }

        return feed;
    }
}
