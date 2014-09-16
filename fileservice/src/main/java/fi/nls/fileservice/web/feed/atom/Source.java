package fi.nls.fileservice.web.feed.atom;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "title", "subtitle", "links", "id", "rights", "updated", "authors" })
public class Source extends CommonAttributes {

    protected URI id;
    protected String title;
    protected String subtitle;
    protected Date updated;
    protected String rights;
    private List<Author> authors = new ArrayList<Author>();
    private List<Link> links = new ArrayList<Link>();

    @XmlElement
    public String getId() {
        if (id != null) {
            return id.toString();
        }
        return null;
    }

    public void setId(URI id) {
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
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @XmlElement
    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    @XmlElement
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @XmlElement(name = "author")
    public List<Author> getAuthors() {
        return authors;
    }

    @XmlElement(name = "link")
    public List<Link> getLinks() {
        return links;
    }
}
