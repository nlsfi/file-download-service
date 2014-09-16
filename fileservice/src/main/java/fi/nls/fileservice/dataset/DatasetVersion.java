package fi.nls.fileservice.dataset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fi.nls.fileservice.files.DetachedNode;

@JsonInclude(Include.NON_EMPTY)
public class DatasetVersion extends DatasetDescription {

    @NotNull
    @Pattern(regexp = "([a-z]|[0-9]|[_-])+", message = "validation.allowed.chars")
    private String name;

    private Dataset dataset;
    private String wmsLayer;
    private String wmsMinScale;
    private String wmsMaxScale;
    // private String gridScale;

    private Calendar lastModified;

    private List<DatasetGridDefinition> gridDefinitions = new ArrayList<DatasetGridDefinition>();
    private boolean isSingleFile;

    private List<DetachedNode> nodes = new ArrayList<DetachedNode>();

    private List<String> formats = new ArrayList<String>();

    private String uri;

    public List<DetachedNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DetachedNode> fileNodes) {
        nodes = fileNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getWmsLayer() {
        return wmsLayer;
    }

    public void setWmsLayer(String wmsLayer) {
        this.wmsLayer = wmsLayer;
    }

    @JsonIgnore
    public String getWmsMinScale() {
        return wmsMinScale;
    }

    public void setWmsMinScale(String wmsMinScale) {
        this.wmsMinScale = wmsMinScale;
    }

    @JsonIgnore
    public String getWmsMaxScale() {
        return wmsMaxScale;
    }

    public void setWmsMaxScale(String wmsMaxScale) {
        this.wmsMaxScale = wmsMaxScale;
    }

    public List<DatasetGridDefinition> getGridDefs() {
        return gridDefinitions;
    }

    public void setGridDefinitions(List<DatasetGridDefinition> gridDefinitions) {
        this.gridDefinitions = gridDefinitions;
    }

    public boolean isSingleFile() {
        return isSingleFile;
    }

    public void setSingleFile(boolean isSingleFile) {
        this.isSingleFile = isSingleFile;
    }

    @JsonIgnore
    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    /*
     * public String getGridScale() { return gridScale; }
     * 
     * public void setGridScale(String gridScale) { this.gridScale = gridScale;
     * }
     */

    public Calendar getLastModified() {
        return lastModified;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
