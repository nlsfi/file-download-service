package fi.nls.fileservice.files;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import fi.nls.fileservice.jcr.repository.MetadataUpdateTask;

public class MetadataUpdateNodeVisitor implements NodeVisitor {

    private MetadataUpdateTask updater;

    public MetadataUpdateNodeVisitor(MetadataUpdateTask updater) {
        this.updater = updater;
    }

    @Override
    public void visitNode(Node node) throws RepositoryException {
        if (node.isNodeType(NodeType.NT_FILE)
                && !node.isNodeType("nls:datasetFile")) {
            updater.processNode(node);
        }
    }

}
