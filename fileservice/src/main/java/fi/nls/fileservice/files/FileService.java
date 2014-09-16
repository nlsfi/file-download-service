package fi.nls.fileservice.files;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.RowIterator;

/**
 * FileService provides access to regular files in a repository
 * 
 */
public interface FileService {

    //FIXME cleanup this interface (remove jcr dependencies and duplicate
    //methods
    
    /**
     * Delete a file
     * @param absPath jcr path to file
     */
    public void delete(String absPath);

    public DetachedNode getNode(String absPath);
    
    public DetachedNode getNode(String absPath, Session session);

    /**
     * Query files and return iterable results
     * This is used when the result file list must be streamed to client without
     * accessing the files beforehand
     * 
     * @param queryParams
     * @return RowIterator, than can be lazily iterated
     */
    public RowIterator queryRows(DatasetQueryParams queryParams);

    public List<DetachedNode> queryNodes(String query, Comparator<Node> comparator);

    public List<DetachedNode> queryNodes(DatasetQueryParams queryParams, Comparator<Node> comparator);

    public void saveProperties(String absPath, List<DetachedProperty> detachedProperties);

    public String saveFile(String parentPath, String name, InputStream in);


}
