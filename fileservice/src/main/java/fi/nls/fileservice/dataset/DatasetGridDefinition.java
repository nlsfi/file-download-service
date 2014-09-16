package fi.nls.fileservice.dataset;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class DatasetGridDefinition {

    @NotNull
    private String crs;

    private String crsLabel;

    private int gridScale;
    private String gridSize;
    private String gridType;
    private int minGridScale;
    private int maxGridScale;

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getCrsLabel() {
        return crsLabel;
    }

    public void setCrsLabel(String crsLabel) {
        this.crsLabel = crsLabel;
    }

    public int getGridScale() {
        return gridScale;
    }

    public void setGridScale(int gridScale) {
        this.gridScale = gridScale;
    }

    public String getGridType() {
        return gridType;
    }

    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    public int getMinGridScale() {
        return minGridScale;
    }

    public void setMinGridScale(int minGridScale) {
        this.minGridScale = minGridScale;
    }

    public int getMaxGridScale() {
        return maxGridScale;
    }

    public void setMaxGridScale(int maxGridScale) {
        this.maxGridScale = maxGridScale;
    }

    public String getGridSize() {
        return gridSize;
    }

    public void setGridSize(String gridSize) {
        this.gridSize = gridSize;
    }
}
