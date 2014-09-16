package fi.nls.fileservice.files;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;

import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.util.Formatter;
import fi.nls.fileservice.util.NodeUtils;

/**
 * DetachedNode contains the information from a JCR node, but is "detached" from
 * active JCR session
 */
public class DetachedNode {

    private String name;
    private String path;
    private String type;
    boolean isFolder = false;

    private long length;
    private String mimeType;
    private Date lastModified;
    private String createdBy;

    private List<DetachedProperty> properties = new ArrayList<DetachedProperty>();
    private List<DetachedNode> childNodes = new ArrayList<DetachedNode>();

    private List<DetachedNode> relatedNodes = new ArrayList<DetachedNode>();

    /**
     * Constructor, creates an empty DetachedNode-object
     */
    public DetachedNode() {

    }

    /**
     * Constructor, copies this <code>javax.jcr.Node</code> properties to this
     * DetachedNode with default value traverseChildren true
     * 
     * @param node
     *            source node
     * @throws RepositoryException
     *             if error occurs reading from the JCR session
     */
    public DetachedNode(Node node) throws RepositoryException {
        this(node, true);
    }

    /**
     * Contstructor, copies this <code>javax.jcr.Node</code> properties to this
     * DetachedNode
     * 
     * @param node
     *            source <code>javax.jcr.Node</code>
     * @param traverseChildren
     *            should this traverse child nodes also
     * @throws RepositoryException
     *             if error occurs
     */
    public DetachedNode(Node node, boolean traverseChildren)
            throws RepositoryException {
        this.name = node.getName();
        this.path = node.getPath();

        this.isFolder = NodeUtils.isFolder(node);
        this.type = node.getPrimaryNodeType().getName();

        if (node.isNodeType(NodeType.NT_FILE)) {
            Node content = node.getNode(Node.JCR_CONTENT);
            this.length = content.getProperty(Property.JCR_DATA).getLength();
            if (content.hasProperty(Property.JCR_MIMETYPE)) {
                this.mimeType = content.getProperty(Property.JCR_MIMETYPE)
                        .getString();
            }
            if (content.hasProperty(Property.JCR_LAST_MODIFIED)) {
                this.lastModified = content
                        .getProperty(Property.JCR_LAST_MODIFIED).getDate()
                        .getTime();
            }
        }
        if (node.hasProperty(Property.JCR_CREATED_BY)) {
            this.createdBy = node.getProperty(Property.JCR_CREATED_BY)
                    .getString();
        }

        PropertyIterator propertyIterator = node.getProperties();
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            if (!(property.getDefinition().isProtected() || property
                    .getDefinition().isAutoCreated())) {
                DetachedProperty detachedProperty = new DetachedProperty(
                        property);
                this.properties.add(detachedProperty);
            }
        }

        if (node.hasProperty(MetadataProperty.NLS_RELATED)) {
            Node parent = node.getParent();
            Value[] values = node.getProperty(MetadataProperty.NLS_RELATED)
                    .getValues();
            for (Value value : values) {
                String name = value.getString();
                if (parent.hasNode(name)) {
                    Node relatedNode = parent.getNode(name);
                    relatedNodes.add(new DetachedNode(relatedNode, false));
                }
            }
        }

        if (traverseChildren) {

            NodeIterator children = node.getNodes();
            while (children.hasNext()) {
                Node childNode = children.nextNode();
                // filter out internal repository nodes that are not files or
                // folders
                if (childNode.isNodeType(NodeType.NT_FOLDER)
                        || childNode.isNodeType(NodeType.NT_FILE)) {
                    childNodes.add(new DetachedNode(childNode, false));
                }
            }
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<DetachedNode> getRelatedNodes() {
        return this.relatedNodes;
    }

    public String[] getPathComponents() {
        if (path != null) {
            // path always starts with '/'
            return path.substring(1).split("/");
        } else {
            return null;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public List<DetachedProperty> getProperties() {
        return properties;
    }

    public List<DetachedNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<DetachedNode> nodes) {
        this.childNodes = nodes;
    }

    public void addProperty(DetachedProperty property) {
        this.properties.add(property);
    }

    public DetachedProperty getProperty(String name) {
        for (DetachedProperty property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    public boolean hasProperty(String name) {
        DetachedProperty dp = getProperty(name);
        return dp != null ? true : false;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public long getLength() {
        return this.length;
    }

    public String getLengthStr() {
        return Formatter.formatLength(length);
    }
}
