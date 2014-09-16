package fi.nls.fileservice.web.feed.atom.builder;

public class FeedMetadata {

    private String metadataUri;
    private String metadataHTMLUri;
    private String metadataMimeType;
    private String metadataHTMLMimeType;
    private String[] languages;

    private String serviceDescriptionFileIdentifier;

    public String getMetadataUri() {
        return metadataUri;
    }

    public void setMetadataUri(String metadataUri) {
        this.metadataUri = metadataUri;
    }

    public String getMetadataHTMLUri() {
        return metadataHTMLUri;
    }

    public void setMetadataHTMLUri(String metadataHTMLUri) {
        this.metadataHTMLUri = metadataHTMLUri;
    }

    public String getMetadataMimeType() {
        return metadataMimeType;
    }

    public void setMetadataMimeType(String metadataMimeType) {
        this.metadataMimeType = metadataMimeType;
    }

    public String getMetadataHTMLMimeType() {
        return metadataHTMLMimeType;
    }

    public void setMetadataHTMLMimeType(String metadataHTMLMimeType) {
        this.metadataHTMLMimeType = metadataHTMLMimeType;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getServiceDescriptionFileIdentifier() {
        return serviceDescriptionFileIdentifier;
    }

    public void setServiceDescriptionFileIdentifier(
            String serviceDescriptionFileIdentifier) {
        this.serviceDescriptionFileIdentifier = serviceDescriptionFileIdentifier;
    }

}
