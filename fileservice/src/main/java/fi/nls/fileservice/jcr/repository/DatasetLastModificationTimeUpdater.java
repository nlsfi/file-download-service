package fi.nls.fileservice.jcr.repository;

import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.jcr.MetadataProperty;

public class DatasetLastModificationTimeUpdater {

    private DatasetDAO dao;

    public DatasetLastModificationTimeUpdater(DatasetDAO dao) {
        this.dao = dao;
    }

    public void updateDatasetUpdateDates(Session session)
            throws RepositoryException {

        // for each dataset version
        // query most recently updated file (regardless of format and crs)
        // and update dataset's last modification date to the same value as the
        // file's
        // if the datasets date is older

        List<Dataset> datasets = dao.getDatasets(session);
        for (Dataset dataset : datasets) {
            List<DatasetVersion> datasetVersions = dataset.getVersions();
            for (DatasetVersion datasetVersion : datasetVersions) {
                DatasetQueryParams dqp = new DatasetQueryParams();
                dqp.setDataset(dataset.getName());
                dqp.setDatasetVersion(datasetVersion.getName());
                dqp.setOrderBy(MetadataProperty.NLS_FILECHANGED);
                dqp.setLimit(1);

                NodeIterator nodes = dao.queryDatasetFiles(dqp.getQuery(),
                        session);
                if (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    if (node.hasProperty(MetadataProperty.NLS_FILECHANGED)) {
                        Calendar datasetFilesLastUpdated = node.getProperty(
                                MetadataProperty.NLS_FILECHANGED).getDate();
                        Calendar datasetLastUpdated = datasetVersion
                                .getLastModified();
                        if (datasetLastUpdated == null
                                || datasetFilesLastUpdated
                                        .compareTo(datasetLastUpdated) != 0) {
                            datasetVersion
                                    .setLastModified(datasetFilesLastUpdated);
                            dao.saveDatasetVersion(dataset.getName(),
                                    datasetVersion, session);
                        }
                    }
                }
            }
        }
    }

}
