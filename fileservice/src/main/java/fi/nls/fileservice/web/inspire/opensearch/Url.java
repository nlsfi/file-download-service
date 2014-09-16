package fi.nls.fileservice.web.inspire.opensearch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Url")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Url {

    private String type;
    private String template;
    private String rel;

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @XmlAttribute
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

}
