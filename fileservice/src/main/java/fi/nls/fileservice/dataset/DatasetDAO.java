package fi.nls.fileservice.dataset;

import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.query.InvalidQueryException;

public interface DatasetDAO {

    public List<Dataset> getDatasets(Session session)
            throws PathNotFoundException, RepositoryException;

    public List<Dataset> getDatasets(boolean publishedOnly, Session session)
            throws PathNotFoundException, RepositoryException;

    public String saveDataset(Dataset dataset, Session session)
            throws ValueFormatException, PathNotFoundException, RepositoryException;

    public Dataset getDatasetById(String datasetName, Session session)
            throws PathNotFoundException, RepositoryException;

    public void deleteDataset(String datasetName, Session session)
            throws AccessDeniedException, PathNotFoundException, RepositoryException;

    public DatasetVersion getDatasetVersion(String datasetName, String name,
            Session session) throws PathNotFoundException, RepositoryException;

    public void saveDatasetVersion(String datasetName, DatasetVersion version, Session session)
            throws PathNotFoundException, RepositoryException;

    public void deleteDatasetVersion(String datasetName, String datasetVersionName, Session session)
            throws AccessDeniedException, RepositoryException;

    public NodeIterator queryDatasetFiles(String query, Session session)
            throws InvalidQueryException, RepositoryException;
}
