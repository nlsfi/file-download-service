package fi.nls.fileservice.jcr.repository;

public interface MetadataUpdateService {

    void updateMetadataAsync(String[] paths);
}
