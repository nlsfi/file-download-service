package fi.nls.fileservice.dataset;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class DatasetFile {

    private String dataset;
    private String datasetVersion;
    private String path;
    private String format;
    private String crs;
    private String gridCell;
    private long length;
    private Date lastModified;

    @JsonIgnore
    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @JsonIgnore
    public String getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (dataset != null) {
            hash += dataset.hashCode() * 10;
        }
        if (datasetVersion != null) {
            hash += datasetVersion.hashCode() * 40;
        }
        if (path != null) {
            hash += path.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof DatasetFile) {
            DatasetFile anotherFile = (DatasetFile) another;
            return anotherFile.hashCode() == this.hashCode();
        }
        return false;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getGridCell() {
        return gridCell;
    }

    public void setGridCell(String gridCell) {
        this.gridCell = gridCell;
    }

}
