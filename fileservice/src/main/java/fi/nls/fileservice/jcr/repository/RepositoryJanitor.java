package fi.nls.fileservice.jcr.repository;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.jcr.CredentialsProvider;

/**
 * RepositoryJanitor is component, that performs different kinds of maintenance
 * and cleanup operations on a JCR repository, such as deleting expired files.
 */
public class RepositoryJanitor {

    private static final Logger logger = LoggerFactory
            .getLogger(RepositoryJanitor.class);

    private Repository repository;
    private CredentialsProvider credentialsProvider;

    public RepositoryJanitor(Repository repository,
            CredentialsProvider credentialsProvider) {
        this.repository = repository;
        this.credentialsProvider = credentialsProvider;
    }

    public void execute() {
        Session session = null;
        try {
            session = repository.login(credentialsProvider.getCredentials());

            // query all directories ("nodes") which have nls:expires property
            // and the property value is in the past
            StringBuilder querySb = new StringBuilder(
                    "SELECT * FROM [nt:base] WHERE [nls:expires] < '");
            querySb.append(System.currentTimeMillis());
            querySb.append("'");

            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(querySb.toString(), Query.JCR_SQL2);
            QueryResult result = q.execute();

            NodeIterator iter = result.getNodes();
            while (iter.hasNext()) {
                Node n = iter.nextNode();
                n.remove();
                logger.info("Removing node at " + n.getPath());
            }
            session.save();

        } catch (LoginException e) {
            logger.error("Can't login to repository", e);
        } catch (RepositoryException e) {
            logger.warn("Error cleaning repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

}
