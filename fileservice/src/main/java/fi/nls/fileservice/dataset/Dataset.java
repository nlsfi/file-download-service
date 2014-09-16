package fi.nls.fileservice.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Dataset extends DatasetDescription {

    @Pattern(regexp = "([a-z]|[0-9]|[_-])+", message = "validation.allowed.chars")
    protected String name;

    @Pattern(regexp = "([a-z]|[0-9]){8}-([a-z]|[0-9]){4}-([a-z]|[0-9]){4}-([a-z]|[0-9]){4}-([a-z]|[0-9]){12}", message = "validation.uuid.required")
    private String fileIdentifier;

    @NotNull
    @Pattern(regexp = "(/([a-z]|[A-Z]|[-_]|[0-9])+)+", message = "validation.invalid.path")
    private String path;

    private List<DatasetVersion> versions = new ArrayList<DatasetVersion>();

    private List<SpatialObjectType> spatialObjectTypes = new ArrayList<SpatialObjectType>();

    private String spatialDatasetIdentifierCode;

    @URL
    private String spatialDatasetIdentifierNamespace;

    private Licence licence;

    private boolean isPublished;

    private String metadataUrl;

    @JsonIgnore
    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<DatasetVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<DatasetVersion> versions) {
        this.versions = versions;
    }

    @JsonIgnore
    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    @JsonIgnore
    public List<SpatialObjectType> getSpatialObjectTypes() {
        return this.spatialObjectTypes;
    }

    public void setSpatialObjectTypes(List<SpatialObjectType> spatialObjectTypes) {
        this.spatialObjectTypes = spatialObjectTypes;
    }

    @JsonIgnore
    public String getSpatialDatasetIdentifierCode() {
        return spatialDatasetIdentifierCode;
    }

    public void setSpatialDatasetIdentifierCode(
            String spatialDatasetIdentifierCode) {
        this.spatialDatasetIdentifierCode = spatialDatasetIdentifierCode;
    }

    @JsonIgnore
    public String getSpatialDatasetIdentifierNamespace() {
        return spatialDatasetIdentifierNamespace;
    }

    public void setSpatialDatasetIdentifierNamespace(
            String spatialDatasetIdentifierNamespace) {
        this.spatialDatasetIdentifierNamespace = spatialDatasetIdentifierNamespace;
    }

    @JsonIgnore
    public Licence getLicence() {
        return this.licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public String getMetadataUrl() {
        return metadataUrl;
    }

    public void setMetadataUrl(String metadataUrl) {
        this.metadataUrl = metadataUrl;
    }

    @Override
    public int hashCode() {
        if (getName() != null) {
            return getName().hashCode();
        }
        return super.hashCode();
    }
}
