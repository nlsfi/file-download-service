package fi.nls.fileservice.dataset.crs;

import java.util.LinkedHashMap;
import java.util.Map;

import fi.nls.fileservice.dataset.DatasetGridDefinition;

public class CrsDefinition {

    private String crsId;
    private String epsgId;
    private String inspireUri;
    private String inspireLabel;

    private Map<String, DatasetGridDefinition> grids = new LinkedHashMap<String, DatasetGridDefinition>();

    public CrsDefinition() {

    }

    public String getCrsId() {
        return crsId;
    }

    public void setCrsId(String crsId) {
        this.crsId = crsId;
    }

    public String getEpsgId() {
        return epsgId;
    }

    public void setEpsgId(String epsgId) {
        this.epsgId = epsgId;
    }

    public String getInspireUri() {
        return inspireUri;
    }

    public void setInspireUri(String inspireUrl) {
        this.inspireUri = inspireUrl;
    }

    public String getInspireLabel() {
        return inspireLabel;
    }

    public void setInspireLabel(String inspirelabel) {
        this.inspireLabel = inspirelabel;
    }

    public String[] gridLabels() {
        return grids.keySet().toArray(new String[grids.size()]);
    }

    public Map<String, DatasetGridDefinition> getGrids() {
        return this.grids;
    }

    public void setGrids(Map<String, DatasetGridDefinition> grids) {
        this.grids = grids;
    }

}
