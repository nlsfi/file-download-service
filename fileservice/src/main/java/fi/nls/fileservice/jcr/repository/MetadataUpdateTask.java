package fi.nls.fileservice.jcr.repository;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.security.jcr.CredentialsProvider;

public class MetadataUpdateTask implements Callable<Object> {

    private static final Logger logger = LoggerFactory
            .getLogger(MetadataUpdateTask.class);

    private String[] paths;
    private Repository repository;
    private MetadataUpdateExecutor executor;
    private CredentialsProvider credentialsProvider;
    private DatasetDAO datasetDAO;

    public MetadataUpdateTask(Repository repository,
            CredentialsProvider credentialsProvider,
            MetadataUpdateExecutor executor, DatasetDAO datasetDAO,
            String[] paths) {
        this.repository = repository;
        this.paths = paths;
        this.credentialsProvider = credentialsProvider;
        this.executor = executor;
        this.datasetDAO = datasetDAO;
    }

    public void applyProperties(Node node, Map<String, Object> properties)
            throws RepositoryException {

        Calendar lastModified = node.getNode(Node.JCR_CONTENT)
                .getProperty(Property.JCR_LAST_MODIFIED).getDate();

        if (node.hasProperty(MetadataProperty.NLS_PREVMODIFIED)) {
            Calendar prevModified = node.getProperty(
                    MetadataProperty.NLS_PREVMODIFIED).getDate();
            if (lastModified.compareTo(prevModified) != 0) {
                node.setProperty(MetadataProperty.NLS_PREVMODIFIED,
                        lastModified);
                node.setProperty(MetadataProperty.NLS_FILECHANGED,
                        new GregorianCalendar());
            }
        } else {
            // backwards compatibility
            // don't push file up in MTP
            // node.setProperty(MetadataProperty.NLS_PREVMODIFIED,
            // lastModified);
            // node.setProperty(MetadataProperty.NLS_FILECHANGED, lastModified);

            // TODO is this right??
            node.setProperty(MetadataProperty.NLS_PREVMODIFIED, lastModified);
            node.setProperty(MetadataProperty.NLS_FILECHANGED,
                    new GregorianCalendar());

        }

        Set<Entry<String, Object>> entrySet = properties.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            if (value instanceof java.util.Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((java.util.Date) value);
                node.setProperty(entry.getKey(), cal);
            } else if (value instanceof String[]) {
                String[] vals = (String[]) value;
                node.setProperty(entry.getKey(), vals);
            } else {
                node.setProperty(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    public boolean processNode(Node node) throws RepositoryException {

        Map<String, Object> properties = executor.processNode(node);

        if (properties != null && !properties.isEmpty()) {
            applyProperties(node, properties);
            logger.info("Updated metadata for node: " + node.getPath());
            return true;
        } else {
            // no strategy, noop but log warning
            logger.warn("No metadata update strategy for " + node.getPath());
        }
        return false;
    }

    @Override
    public Object call() {

        logger.debug("Starting metadata update task..");

        Session session = null;
        try {
            session = repository.login(credentialsProvider.getCredentials());

            for (int i = 0; i < paths.length; i++) {
                try {

                    logger.debug("Metadata update for: " + paths[i]);
                    Node node = session.getNode(paths[i]);
                    if (processNode(node)) {

                        // save session in batches of ten files
                        if (i % 10 == 0) {
                            session.save();
                        }
                    }
                } catch (PathNotFoundException pnfe) {
                    logger.warn("File not found: " + paths[i]);
                    // noop, continuining

                } catch (ScriptExecutionException sce) {
                    logger.warn("Error updating metadata for " + paths[i], sce);
                    // noop, continuining
                }
            }

            session.save();

            DatasetLastModificationTimeUpdater dcu = new DatasetLastModificationTimeUpdater(
                    datasetDAO);
            dcu.updateDatasetUpdateDates(session);
            session.save();

            logger.info("Dataset modification times updated.");

        } catch (Exception e) {
            logger.warn("Error updating metadata:", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        return null;
    }

}
