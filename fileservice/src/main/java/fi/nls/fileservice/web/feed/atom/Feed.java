package fi.nls.fileservice.web.feed.atom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="feed", namespace="http://www.w3.org/2005/Atom")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Feed extends Source {

    private List<Entry> entries = new ArrayList<Entry>();

    @XmlElementRef
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * This overrides the default list implementation with another, usually with
     * a lazy loading version
     * 
     * @param entries
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

}
