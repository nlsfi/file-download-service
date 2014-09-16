package fi.nls.fileservice.files;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropertiesFilter removes properties from DetachedNode-objects. Allowed
 * properties can be parametrized.
 * 
 * This is used to hide internal properties from the public customer UI.
 * 
 */
public class PropertiesFilter {

    private static final Logger logger = LoggerFactory
            .getLogger(PropertiesFilter.class);

    private String[] allowedProperties;

    /**
     * Creates a new PropertiesFilter.
     * 
     * @param allowedProperties
     *            String array of allowed properties, all other properties will
     *            be removed
     */
    public PropertiesFilter(String[] allowedProperties) {
        if (allowedProperties == null) {
            throw new IllegalArgumentException(
                    "null allowedproperties passed for constructor");
        }
        this.allowedProperties = allowedProperties;
    }

    /**
     * Filters DetachedNode and it's children
     * 
     * @param node
     *            DetachedNode to be filtered
     */
    public void filter(DetachedNode node) {

        List<DetachedProperty> properties = node.getProperties();
        filterProperties(properties);

        List<DetachedNode> childNodes = node.getChildNodes();
        for (DetachedNode childNode : childNodes) {
            filterProperties(childNode.getProperties());
        }
    }

    protected void filterProperties(List<DetachedProperty> properties) {
        Iterator<DetachedProperty> propertyIterator = properties.iterator();
        while (propertyIterator.hasNext()) {
            DetachedProperty property = propertyIterator.next();
            if (Arrays.binarySearch(allowedProperties, property.getName()) < 0) {
                logger.debug("Filtering property: " + property.getName());
                propertyIterator.remove();
            }
        }
    }

}
