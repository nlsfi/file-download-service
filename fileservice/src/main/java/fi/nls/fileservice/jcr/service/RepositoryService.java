package fi.nls.fileservice.jcr.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RepositoryService {

    void reindex(String path);

    void importRepository(InputStream in) throws IOException;

    void exportRepository(OutputStream out) throws IOException;
}
