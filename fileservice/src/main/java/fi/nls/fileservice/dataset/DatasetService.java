package fi.nls.fileservice.dataset;

import java.util.List;

public interface DatasetService {

    public List<Dataset> getAllDatasets();

    public List<Dataset> getPublishedDatasets();

    public Dataset getDatasetById(String datasetName);

    public void saveDataset(Dataset dataset);

    public void deleteDataset(String datasetName);

    public DatasetVersion getDatasetVersion(String datasetName, String datasetVersion);

    public void saveDatasetVersion(String datasetName, DatasetVersion dataset);

    public void deleteDatasetVersion(String datasetName, String datasetVersion);
}
