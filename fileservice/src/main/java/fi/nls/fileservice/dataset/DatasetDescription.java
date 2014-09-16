package fi.nls.fileservice.dataset;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class DatasetDescription {

    @JsonIgnore
    protected String metadataPath;

    @NotNull
    @Size(min = 1)
    protected Map<String, String> translatedTitles = new HashMap<String, String>();

    private String title;

    @JsonIgnore
    public Map<String, String> getTranslatedTitles() {
        return translatedTitles;
    }

    public void setTranslatedTitles(Map<String, String> translatedTitles) {
        this.translatedTitles = translatedTitles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetadataPath() {
        return this.metadataPath;
    }

    public void setMetadataPath(String path) {
        this.metadataPath = path;
    }

}
