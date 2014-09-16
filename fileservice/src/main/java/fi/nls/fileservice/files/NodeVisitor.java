package fi.nls.fileservice.files;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface NodeVisitor {

    void visitNode(Node node) throws RepositoryException;
}
