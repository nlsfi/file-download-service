package fi.nls.fileservice.jcr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.modeshape.jcr.api.BackupOptions;
import org.modeshape.jcr.api.Problem;
import org.modeshape.jcr.api.Problems;
import org.modeshape.jcr.api.RepositoryManager;
import org.modeshape.jcr.api.RestoreOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.security.jcr.CredentialsProvider;

public class ModeShapeRepositoryService implements RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(ModeShapeRepositoryService.class);

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
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void backupRepository(final String backupBaseDir, final boolean includeBinaries) {
        Session session = null;
        try {

            File backupDir = new File(backupBaseDir);
            if (!backupDir.exists()) {
                if (!backupDir.mkdirs()) {
                    throw new DataAccessException("Unable to create directory: " + backupBaseDir);
                }
            } else {
                if (!backupDir.canWrite()) {
                    throw new DataAccessException("No write permission to path: " + backupBaseDir);
                }
                
                if (!backupDir.isDirectory() || (backupDir.list() != null && backupDir.list().length > 0)) {
                    throw new DataAccessException("Backup directory already exists, backup NOT performed to: " + backupBaseDir);
                }
            }

            session = repository.login(credentialsProvider.getCredentials());
            RepositoryManager repoMgr = ((org.modeshape.jcr.api.Session) session)
                    .getWorkspace().getRepositoryManager();
            Problems problems = repoMgr.backupRepository(backupDir,
                    new BackupOptions() {

                        @Override
                        public boolean includeBinaries() {
                            return includeBinaries;
                        }
                    });

            if (problems.hasProblems()) {
                Iterator<Problem> pIter = problems.iterator();
                while (pIter.hasNext()) {
                    Problem p = pIter.next();
                    logger.warn(p.getMessage(), p.getThrowable());
                }
            }
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void restoreRepository(final String backupBaseDir,
            final boolean includeBinaries, final boolean reindexContentOnFinish) {
        Session session = null;
        try {

            File backupDir = new File(backupBaseDir);
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                throw new DataAccessException("Backup directory " + backupBaseDir + " doesn't exist or is not a directory.");
            }

            session = repository.login(credentialsProvider.getCredentials());
            RepositoryManager repoMgr = ((org.modeshape.jcr.api.Session) session)
                    .getWorkspace().getRepositoryManager();

            Problems problems = repoMgr.restoreRepository(backupDir,
                    new RestoreOptions() {

                        @Override
                        public boolean includeBinaries() {
                            return includeBinaries;
                        }

                        @Override
                        public boolean reindexContentOnFinish() {
                            return reindexContentOnFinish;
                        }

                    });

            if (problems.hasProblems()) {
                Iterator<Problem> pIter = problems.iterator();
                while (pIter.hasNext()) {
                    Problem p = pIter.next();
                    logger.warn(p.getMessage(), p.getThrowable());
                }
            }
        } catch (RepositoryException e) {
            throw new DataAccessException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

}
