package fi.nls.fileservice.files;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.PermissionDeniedException;

public class FileServiceImpl implements FileService {

    private Repository repository;

    /**
     * Constructor
     * 
     * @param repository JCR repository
     */
    public FileServiceImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(String absPath) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());

            // get javax.jcr.Item so we can delete both nodes and properties
            Item item = session.getItem(absPath);
            if (item.isNode()) {
                Node node = (Node) item;
                // check that we are trying to delete a file (not directories or
                // anything else.)
                // removing of completed directories is not implemented
                if (!node.getPrimaryNodeType().isNodeType(NodeType.NT_FILE)) {
                    throw new PermissionDeniedException(
                            "Won't delete node of type: "
                                    + node.getPrimaryNodeType().getName());
                }
                node.remove();
                session.save();
            }

        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (RepositoryException e) {
            e.printStackTrace();
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public RowIterator queryRows(DatasetQueryParams queryParams) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());

            QueryManager queryManager = session.getWorkspace()
                    .getQueryManager();
            Query query = queryManager.createQuery(queryParams.getQuery(),
                    queryParams.getQueryType());

            QueryResult result = query.execute();
            return result.getRows();

        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (InvalidQueryException iqe) {
            throw new IllegalArgumentException(iqe);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            // cannot logout, RowIterator is attached to session..
        }
    }

    @Override
    public List<DetachedNode> queryNodes(DatasetQueryParams queryParams,
            Comparator<Node> comparator) {
        return queryNodes(queryParams.getQuery(), comparator);
    }

    @Override
    public List<DetachedNode> queryNodes(String queryStr, Comparator<Node> comparator) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(queryStr, Query.JCR_SQL2);
            QueryResult result = query.execute();

            List<Node> nodeList = new ArrayList<Node>();
            NodeIterator nodeIterator = result.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                nodeList.add(node);
            }

            if (comparator != null && nodeList.size() > 1) {
                Collections.sort(nodeList, comparator);
            }

            List<DetachedNode> files = new ArrayList<DetachedNode>();
            for (Node node : nodeList) {
                files.add(new DetachedNode(node));
            }

            return files;

        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    @Override
    public DetachedNode getNode(String absPath, Session session) {
        try {
            Node node = session.getNode(absPath);
            return new DetachedNode(node);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        }

    }

    @Override
    public DetachedNode getNode(String absPath) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            Node node = session.getNode(absPath);
            return new DetachedNode(node);
        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void saveProperties(String absPath,
            List<DetachedProperty> detachedProperties) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            Node node = session.getNode(absPath);

            Map<String, PropertyDefinition> propertyToNodeType = new HashMap<String, PropertyDefinition>();
            NodeTypeIterator mixins = node.getSession().getWorkspace()
                    .getNodeTypeManager().getMixinNodeTypes();
            while (mixins.hasNext()) {
                NodeType mixin = mixins.nextNodeType();
                PropertyDefinition[] pds = mixin.getPropertyDefinitions();
                for (PropertyDefinition pd : pds) {
                    propertyToNodeType.put(pd.getName(), pd);
                }
            }

            for (DetachedProperty detachedProperty : detachedProperties) {
                PropertyDefinition def = propertyToNodeType
                        .get(detachedProperty.getName());
                if (def != null) {

                    if (node.hasProperty(detachedProperty.getName())) {
                        node.getProperty(detachedProperty.getName()).remove();
                    }

                    // add mixin if necessary
                    NodeType requiredNodeType = def.getDeclaringNodeType();

                    if (!node.isNodeType(requiredNodeType.getName())) {
                        node.addMixin(requiredNodeType.getName());
                    }

                    ValueFactory valueFactory = session.getValueFactory();
                    List<String> strValues = detachedProperty.getValues();
                    Value[] values = new Value[strValues.size()];

                    for (int i = 0; i < strValues.size(); i++) {
                        values[i] = valueFactory.createValue(strValues.get(i));
                    }

                    if (values.length == 1 && !def.isMultiple()) {
                        node.setProperty(detachedProperty.getName(), values[0]);
                    } else {
                        node.setProperty(detachedProperty.getName(), values);
                    }
                } // else unknown property

            }

            session.save();
        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (AccessDeniedException e) {
            throw new PermissionDeniedException(e);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public String saveFile(String parentPath, String name,
            InputStream contentStream) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            Node parentNode = session.getNode(parentPath);

            Node fileNode = null;
            if (parentNode.hasNode(name)) {
                fileNode = parentNode.getNode(name);
                if (!fileNode.getPrimaryNodeType().isNodeType(NodeType.NT_FILE)) {
                    throw new DataAccessException("Target + "+ fileNode.getPath() + " of type " 
                            + fileNode.getPrimaryNodeType() +" is not a file.");
                }
            } else { // new file
                fileNode = parentNode.addNode(name, NodeType.NT_FILE);
            }

            Node contentNode = null;
            if (fileNode.hasNode(Node.JCR_CONTENT)) {
                contentNode = fileNode.getNode(Node.JCR_CONTENT);
            } else {
                contentNode = fileNode.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);
            }

            ValueFactory valueFactory = session.getValueFactory();
            contentNode.setProperty(Property.JCR_DATA,
                    valueFactory.createBinary(contentStream));
            session.save();

            return fileNode.getPath();
        } catch (LoginException e) {
            throw new PermissionDeniedException(e);
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

}
