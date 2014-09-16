package fi.nls.fileservice.dataset;

public class SpatialObjectType {

    private String uri;
    private String type;

    public SpatialObjectType() {

    }

    public SpatialObjectType(String uri) {
        this.uri = uri;
    }

    public SpatialObjectType(String uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
