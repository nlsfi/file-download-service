package fi.nls.fileservice.jcr.repository;

import java.util.concurrent.ExecutorService;

import javax.jcr.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.security.jcr.CredentialsProvider;

public class MetadataUpdateServiceImpl implements MetadataUpdateService {

    private static final Logger logger = LoggerFactory
            .getLogger(MetadataUpdateServiceImpl.class);

    private Repository repository;
    private ExecutorService executor;
    private CredentialsProvider credentialsProvider;
    private ScriptProvider scriptProvider;
    private DatasetDAO datasetDAO;

    public MetadataUpdateServiceImpl(ExecutorService executor,
            ScriptProvider scriptProvider, Repository repository,
            CredentialsProvider credentialsProvider, DatasetDAO datasetDAO) {
        this.executor = executor;
        this.repository = repository;
        this.credentialsProvider = credentialsProvider;
        this.scriptProvider = scriptProvider;
        this.datasetDAO = datasetDAO;
    }

    @Override
    public void updateMetadataAsync(String[] paths) {
        JavascriptMetadataServiceExecutor javascriptExecutor = new JavascriptMetadataServiceExecutor();
        javascriptExecutor.setScriptProvider(scriptProvider);
        javascriptExecutor.init();

        MetadataUpdateTask task = new MetadataUpdateTask(repository,
                credentialsProvider, javascriptExecutor, datasetDAO, paths);

        executor.submit(task);
        logger.info("Started metadata update for : {} file(s)", paths.length);
    }

    public void shutdown() {
        executor.shutdownNow();
    }

}
