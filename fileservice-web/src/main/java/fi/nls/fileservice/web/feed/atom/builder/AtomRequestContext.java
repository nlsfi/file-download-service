package fi.nls.fileservice.web.feed.atom.builder;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class AtomRequestContext {

    private FeedMetadata feedMetadata;
    private Locale locale;
    private UriComponents uriComponents;
    private MessageSource messageSource;

    public FeedMetadata getFeedMetadata() {
        return feedMetadata;
    }

    public void setFeedMetadata(FeedMetadata feedMetadata) {
        this.feedMetadata = feedMetadata;
    }

    public String getLanguage() {
        return this.locale.getLanguage();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setUriComponents(UriComponents uriComponents) {
        this.uriComponents = uriComponents;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String msg, Object[] values) {
        return messageSource.getMessage(msg, values, null, locale);
    }

    public UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder.fromUri(uriComponents.toUri());
    }

}
