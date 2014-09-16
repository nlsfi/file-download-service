package fi.nls.fileservice.files;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * DetachedProperty contains the information of a JCR node, but is detached from
 * active JCR session.
 * 
 */
public class DetachedProperty {

    private boolean isMultiple;
    private String name;
    private List<String> values = new ArrayList<String>();
    private boolean isProtected;

    /**
     * Creates a empty DetachedNode object
     */
    public DetachedProperty() {

    }

    /**
     * Creates a DetachedNode object and initializes it from a
     * <code>javax.jcr.Property</code>
     */
    public DetachedProperty(Property property) throws RepositoryException {
        this.isMultiple = property.getDefinition().isMultiple();
        this.name = property.getName();
        if (property.getName().startsWith("jcr:")) {
            this.isProtected = true;
        }

        // TODO cleanup
        if (isMultiple) {
            Value[] propertyValues = property.getValues();
            for (Value v : propertyValues) {
                if (!(v.getType() == PropertyType.BINARY)) {
                    this.values.add(v.getString());
                }
            }
        } else {
            Value v = property.getValue();
            if (v.getType() == PropertyType.DATE) {
                Calendar date = v.getDate();
                DateTime lastMod = new DateTime(date.getTimeInMillis());
                DateTimeFormatter fmt = DateTimeFormat
                        .forPattern("yyyy-MM-dd HH:mm");
                this.values.add(lastMod.toString(fmt));
            } else if (v.getType() == PropertyType.BINARY) {
                this.values.add(Long.toString(v.getBinary().getSize()));
            } else {
                this.values.add(v.getString());
            }
        }
    }

    /**
     * Returns true if this is a multivalue property
     * 
     * @return <code>true</code> if multivalue, <code>false</code> otherwise
     */
    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setValue(String value) {
        values.add(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return values.get(0);
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

}
