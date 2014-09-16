package fi.nls.fileservice.web.feed.atom;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "entry", namespace = "http://www.w3.org/2005/Atom")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "spatialDatasetIdentifierCode",
        "spatialDatasetIdentifierNamespace", "title", "links", "updated", "id",
        "categories", "summary", "rights", "content", "distributionFormats" })
public class Entry extends CommonAttributes {

    // ATOM STANDARD
    protected String id;
    protected String title;
    protected Date updated;
    protected String rights;
    protected String summary;
    protected List<Link> links = new ArrayList<Link>();
    protected List<Category> categories = new ArrayList<Category>();
    protected String content;

    // INSPIRE
    protected String spatialDatasetIdentifierCode;
    protected String spatialDatasetIdentifierNamespace;

    // NLSFI
    protected List<String> distributionFormats = new ArrayList<String>();

    @XmlElement
    public String getId() {
        return id.toString();
    }

    public void setId(URI id) {
        this.id = id.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @XmlElement
    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    @XmlElement
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @XmlElement(name="link")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @XmlElement(name="category")
    public List<Category> getCategories() {
        return this.categories;
    }

    @XmlElement(name="content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @XmlElement(name="spatial_dataset_identifier_code", namespace="http://inspire.ec.europa.eu/schemas/inspire_dls/1.0")
    public String getSpatialDatasetIdentifierCode() {
        return spatialDatasetIdentifierCode;
    }

    public void setSpatialDatasetIdentifierCode(
            String spatialDatasetIdentifierCode) {
        this.spatialDatasetIdentifierCode = spatialDatasetIdentifierCode;
    }

    @XmlElement(name="spatial_dataset_identifier_namespace", namespace="http://inspire.ec.europa.eu/schemas/inspire_dls/1.0")
    public String getSpatialDatasetIdentifierNamespace() {
        return spatialDatasetIdentifierNamespace;
    }

    public void setSpatialDatasetIdentifierNamespace(
            String spatialDatasetIdentifierNamespace) {
        this.spatialDatasetIdentifierNamespace = spatialDatasetIdentifierNamespace;
    }

    @XmlElement(name="distributionFormat", namespace="http://xml.nls.fi/download-service/2013/03")
    public List<String> getDistributionFormats() {
        return distributionFormats;
    }

}
