package fi.nls.fileservice.web.feed.atom;

import javax.xml.bind.annotation.XmlAttribute;

public class CommonAttributes {

    private String lang;

    public void setLang(String lang) {
        this.lang = lang;
    }

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    public String getLang() {
        return lang;
    }

}
