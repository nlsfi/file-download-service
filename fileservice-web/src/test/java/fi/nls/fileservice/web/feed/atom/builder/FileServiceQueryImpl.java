package fi.nls.fileservice.web.feed.atom.builder;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.RowIterator;

import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.DetachedProperty;
import fi.nls.fileservice.files.FileService;

public class FileServiceQueryImpl implements FileService {

    private List<DetachedNode> nodes;

    public FileServiceQueryImpl(List<DetachedNode> files) {
        this.nodes = files;
    }

    @Override
    public void delete(String absPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public DetachedNode getNode(String absPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveProperties(String absPath,
            List<DetachedProperty> detachedProperties) {
        // TODO Auto-generated method stub

    }

    @Override
    public String saveFile(String parentPath, String name, InputStream in) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @Override public String createShareFolder(String parentPath, String name,
     * String uid, int daysValid) { // TODO Auto-generated method stub return
     * null; }
     */

    @Override
    public List<DetachedNode> queryNodes(DatasetQueryParams queryParams,
            Comparator<Node> comparator) {
        return queryNodes(queryParams.getQuery(), comparator);
    }

    @Override
    public List<DetachedNode> queryNodes(String queryStr,
            Comparator<Node> comparator) {
        return nodes;
    }

    @Override
    public DetachedNode getNode(String absPath, Session session) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RowIterator queryRows(DatasetQueryParams queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @Override public void visitNodes(String absPath, NodeVisitor visitor) {
     * // TODO Auto-generated method stub
     * 
     * }
     */
}
