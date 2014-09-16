package fi.nls.fileservice.jcr.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.security.jcr.CredentialsProvider;

public class ModeShapeRepositoryService implements RepositoryService {

    private Repository repository;
    private CredentialsProvider credentialsProvider;
    private String metaPath;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setMetaPath(String metaPath) {
        this.metaPath = metaPath;
    }

    @Override
    public void reindex(String path) {

        try {
            Session session = repository.login(credentialsProvider
                    .getCredentials());

            // using vendor specific ModeShape API so casting..
            Workspace workspace = session.getWorkspace();
            if (workspace instanceof org.modeshape.jcr.api.Workspace) {
                org.modeshape.jcr.api.Workspace modeshapeWorkspace = (org.modeshape.jcr.api.Workspace) session
                        .getWorkspace();

                if (path == null || "".equals(path)) {
                    modeshapeWorkspace.reindexAsync();
                } else {
                    modeshapeWorkspace.reindexAsync(path);
                }

            } else {
                throw new RuntimeException("Error workspace class is "
                        + workspace.getClass().getName()
                        + " expected org.modeshape.jcr.api.Workspace");
            }
        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        }

    }

    @Override
    public void importRepository(InputStream in) throws IOException {

        // sanity check
        if (metaPath == null || !metaPath.startsWith("/")) {
            throw new IllegalStateException("BUG: Metadata path not set: |"
                    + metaPath + "|");
        }

        Session session = null;
        try {
            session = repository.login(credentialsProvider.getCredentials());

            // remove existing nodes (importXML doesn't do this),
            // this is to ensure the repository matches the import exactly
            Node metaRoot = session.getNode(metaPath);
            metaRoot.remove();

            // have to use root path here, otherwise /meta/meta follows
            session.importXML("/", in,
                    ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
            session.save();

        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void exportRepository(OutputStream out) throws IOException {

        // sanity check
        if (metaPath == null || !metaPath.startsWith("/")) {
            throw new IllegalStateException("BUG: Metadata path not set: |"
                    + metaPath + "|");
        }

        Session session = null;
        try {
            session = repository.login(credentialsProvider.getCredentials());
            session.exportSystemView(this.metaPath, out, true, false);
            session.logout();
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

}
