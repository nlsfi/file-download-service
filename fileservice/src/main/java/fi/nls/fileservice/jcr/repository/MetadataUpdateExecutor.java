package fi.nls.fileservice.jcr.repository;

import java.util.Map;

import javax.jcr.Node;

public interface MetadataUpdateExecutor {

    public Map<String, Object> processNode(Node node);

    public void init();

}
