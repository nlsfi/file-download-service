package fi.nls.fileservice.web.feed.atom;

import javax.xml.bind.annotation.XmlValue;

public class Crs {

    private String crs;

    @XmlValue
    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

}
