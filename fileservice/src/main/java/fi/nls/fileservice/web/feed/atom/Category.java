package fi.nls.fileservice.web.feed.atom;

import javax.xml.bind.annotation.XmlAttribute;

public class Category extends CommonAttributes {

    private String term;
    private String scheme;
    private String label;

    @XmlAttribute
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @XmlAttribute
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @XmlAttribute
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
