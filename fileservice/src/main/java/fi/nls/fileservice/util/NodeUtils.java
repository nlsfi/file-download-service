package fi.nls.fileservice.util;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

/**
 * Helper methods for JCR node handling
 * 
 */
public class NodeUtils {

    /**
     * Check if the given node is a file system folder (JCR: nt:folder)
     * 
     * @param node
     *            JCR node
     * @return <code>true</code> if node is a directory, <code>false</code>
     *         otherwise
     * @throws RepositoryException
     */
    public static boolean isFolder(Node node) throws RepositoryException {
        NodeType nt = node.getPrimaryNodeType();
        return (nt.isNodeType(NodeType.NT_FOLDER) || nt.isNodeType("mode:root"));
    }

    public static Node addChildIfDoesntExist(Node root, String child,
            String type) throws PathNotFoundException, RepositoryException {
        if (root.hasNode(child)) {
            return root.getNode(child);
        } else {
            return root.addNode(child, type);
        }
    }

}
