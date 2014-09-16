package fi.nls.fileservice.web.inspire.opensearch;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OpenSearchDescription", namespace = "http://a9.com/-/spec/opensearch/1.1/")
@XmlAccessorType(XmlAccessType.PROPERTY)
// @XmlType(propOrder = {"ShortName", "Description", "Url", "Contact",
// "Language"})
public class OpenSearchDescription {

    private String shortName;
    private String description;
    private String contact;

    private List<Url> urls = new LinkedList<Url>();

    private List<String> languages = new LinkedList<String>();

    @XmlElement(name = "ShortName")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @XmlElementRef
    public List<Url> getUrls() {
        return urls;
    }

    @XmlElement(name = "Contact")
    public String getContact() {
        return contact;
    }

    @XmlElement(name = "Language")
    public List<String> getLanguages() {
        return languages;
    }

}
