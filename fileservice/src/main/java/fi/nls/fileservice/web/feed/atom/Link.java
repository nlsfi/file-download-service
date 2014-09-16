package fi.nls.fileservice.web.feed.atom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="link")
public class Link {

    protected String href;
    protected String hreflang;
    protected String rel;
    protected String length;
    protected String type;
    protected String title;

    @XmlAttribute
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @XmlAttribute
    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @XmlAttribute
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    @XmlAttribute
    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
