package fi.nls.fileservice.dataset.jcr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.dataset.DatasetGridDefinition;
import fi.nls.fileservice.dataset.DatasetProperty;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.dataset.SpatialObjectType;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.util.NodeUtils;

public class JCRDatasetDAO implements DatasetDAO {

    private static final Logger logger = LoggerFactory.getLogger(JCRDatasetDAO.class);

    public static final String VERSIONS_PATH = "versions";
    public static final String TITLES_PATH = "titles";
    public static final String CRS_PATH = "crs";

    private Map<String, CrsDefinition> crsDefinitions;

    private String metaRootPath;

    public JCRDatasetDAO(String rootpath, Map<String, CrsDefinition> crsDefinitions) {
        this.metaRootPath = rootpath;
        this.crsDefinitions = crsDefinitions;
    }

    @Override
    public List<Dataset> getDatasets(Session session)
            throws PathNotFoundException, RepositoryException {
        return getDatasets(false, session);
    }

    @Override
    public List<Dataset> getDatasets(boolean publishedOnly, Session session)
            throws PathNotFoundException, RepositoryException {

        QueryManager qm = session.getWorkspace().getQueryManager();

        // must use query here instead of node.getNodes() to allow for child nodes
        // where this session has no access rights
        StringBuilder queryStr = new StringBuilder("SELECT * FROM [nls:dataset]");
        if (publishedOnly) {
            queryStr.append(" WHERE [");
            queryStr.append(DatasetProperty.IS_PUBLISHED);
            queryStr.append("] ='true'");
        }

        logger.debug("Executing query: |{}| " + queryStr);

        Query query = qm.createQuery(queryStr.toString(), Query.JCR_SQL2);
        QueryResult result = query.execute();

        List<Dataset> datasets = new ArrayList<Dataset>();

        /**
         * Apparently result.getNodes() : NodeIterator throws
         * PermissionDeniedException if access is not allowed to any of the
         * result nodes. result.getRows() => rows.getNode() works cleanly
         * skipping the nodes that the user doesn't have read permissions to
         */
        RowIterator iter = result.getRows();
        while (iter.hasNext()) {
            Row row = iter.nextRow();
            Node datasetNode = row.getNode();
            Dataset dataset = getDatasetFromJCRNode(datasetNode);
            datasets.add(dataset);
        }

        logger.debug("Found {} datasets with query {} and user {}",
                datasets.size(), queryStr, session.getUserID());

        return datasets;
    }

    private DatasetVersion getDatasetVersionFromJCRNode(Node node)
            throws PathNotFoundException, RepositoryException {
        DatasetVersion datasetVersion = new DatasetVersion();
        datasetVersion.setName(node.getName());

        if (node.hasNode(DatasetProperty.TITLES)) {
            Node titles = node.getNode(DatasetProperty.TITLES);
            NodeIterator titleNodes = titles.getNodes();
            while (titleNodes.hasNext()) {
                Node tnode = titleNodes.nextNode();
                if (tnode.hasProperty(DatasetProperty.VALUE)) {
                    datasetVersion.getTranslatedTitles().put(
                            tnode.getName(),
                            tnode.getProperty(DatasetProperty.VALUE)
                                    .getString());
                }
            }
        }

        if (node.hasProperty(MetadataProperty.NLS_WMSMINSCALE)) {
            datasetVersion.setWmsMinScale(node.getProperty(
                    MetadataProperty.NLS_WMSMINSCALE).getString());
        }
        if (node.hasProperty(MetadataProperty.NLS_WMSMAXSCALE)) {
            datasetVersion.setWmsMaxScale(node.getProperty(
                    MetadataProperty.NLS_WMSMAXSCALE).getString());
        }
        if (node.hasProperty(MetadataProperty.NLS_WMSLAYER)) {
            datasetVersion.setWmsLayer(node.getProperty(
                    MetadataProperty.NLS_WMSLAYER).getString());
        }
        if (node.hasProperty(MetadataProperty.LASTMODIFIED)) {
            datasetVersion.setLastModified(node.getProperty(
                    MetadataProperty.LASTMODIFIED).getDate());
        }

        if (node.hasProperty(MetadataProperty.GMD_DISTRIBUTIONFORMAT)) {
            Value[] values = node.getProperty(
                    MetadataProperty.GMD_DISTRIBUTIONFORMAT).getValues();
            for (Value v : values) {
                datasetVersion.getFormats().add(v.getString());
            }
        }

        if (node.hasNode(DatasetProperty.CRS)) {
            Node crsRoot = node.getNode(DatasetProperty.CRS);
            if (crsRoot.hasNodes()) {
                NodeIterator crss = crsRoot.getNodes();
                while (crss.hasNext()) {
                    Node crs = crss.nextNode();

                    String crsString = crs.getName().replaceAll("\\$", ":");
                    CrsDefinition def = this.crsDefinitions.get(crsString);

                    DatasetGridDefinition gridDef = null;
                    if (crs.hasProperty(DatasetProperty.NLS_GRIDSIZE)) {
                        String gridSize = crs.getProperty(
                                DatasetProperty.NLS_GRIDSIZE).getString();
                        if (!"None".equals(gridSize)) {
                            gridDef = def.getGrids().get(gridSize);
                        }
                    }

                    if (gridDef == null) {
                        gridDef = new DatasetGridDefinition();
                        gridDef.setCrs(crsString);
                        datasetVersion.setSingleFile(true);
                    }

                    datasetVersion.getGridDefs().add(gridDef);
                }
            }
        }
        return datasetVersion;
    }

    private Dataset getDatasetFromJCRNode(Node datasetNode)
            throws PathNotFoundException, RepositoryException {
        return this.getDatasetFromJCRNode(datasetNode, true);
    }

    private Dataset getDatasetFromJCRNode(Node datasetNode,
            boolean fetchDatasetVersions) throws PathNotFoundException,
            RepositoryException {

        Dataset dataset = new Dataset();
        dataset.setName(datasetNode.getName());
        dataset.setMetadataPath(datasetNode.getPath());

        if (datasetNode.hasProperty(MetadataProperty.GMD_FILEIDENTIFIER)) {
            dataset.setFileIdentifier(datasetNode.getProperty(
                    MetadataProperty.GMD_FILEIDENTIFIER).getString());
        }

        if (datasetNode.hasNode(DatasetProperty.TITLES)) {
            Node titles = datasetNode.getNode(DatasetProperty.TITLES);
            if (titles.hasNodes()) {
                NodeIterator titleNodes = titles.getNodes();
                while (titleNodes.hasNext()) {
                    Node tnode = titleNodes.nextNode();
                    if (tnode.hasProperty(DatasetProperty.VALUE)) {
                        dataset.getTranslatedTitles().put(
                                tnode.getName(),
                                tnode.getProperty(DatasetProperty.VALUE)
                                        .getString());
                    }
                }
            }
        }

        if (datasetNode
                .hasProperty(DatasetProperty.SPATIAL_DATASET_IDENTIFIER_CODE)) {
            dataset.setSpatialDatasetIdentifierCode(datasetNode.getProperty(
                    DatasetProperty.SPATIAL_DATASET_IDENTIFIER_CODE)
                    .getString());
        }

        if (datasetNode
                .hasProperty(DatasetProperty.SPATIAL_DATASET_IDENTIFIER_NAMESPACE)) {
            dataset.setSpatialDatasetIdentifierNamespace(datasetNode
                    .getProperty(
                            DatasetProperty.SPATIAL_DATASET_IDENTIFIER_NAMESPACE)
                    .getString());
        }

        if (datasetNode.hasProperty(DatasetProperty.SPATIAL_OBJECT_TYPE)) {
            Value[] values = datasetNode.getProperty(
                    DatasetProperty.SPATIAL_OBJECT_TYPE).getValues();
            for (Value v : values) {
                dataset.getSpatialObjectTypes().add(
                        new SpatialObjectType(v.getString()));
            }
        }

        if (datasetNode.hasProperty(DatasetProperty.PATH)) {
            dataset.setPath(datasetNode.getProperty(DatasetProperty.PATH)
                    .getString());
        }

        if (datasetNode.hasProperty(DatasetProperty.IS_PUBLISHED)) {
            dataset.setPublished(datasetNode.getProperty(
                    DatasetProperty.IS_PUBLISHED).getBoolean());
        }

        if (datasetNode.hasProperty(DatasetProperty.LICENSE)) {
            dataset.setLicence(Licence.valueOf(datasetNode.getProperty(
                    DatasetProperty.LICENSE).getString()));
        } else {
            dataset.setLicence(Licence.RESTRICTED);
        }

        if (fetchDatasetVersions) {
            if (datasetNode.hasNode(DatasetProperty.VERSIONS)) {
                Node versionRoot = datasetNode
                        .getNode(DatasetProperty.VERSIONS);
                if (versionRoot.hasNodes()) {
                    NodeIterator versionNodes = versionRoot.getNodes();
                    while (versionNodes.hasNext()) {
                        Node versionNode = versionNodes.nextNode();
                        DatasetVersion datasetVersion = this
                                .getDatasetVersionFromJCRNode(versionNode);
                        datasetVersion.setDataset(dataset);
                        dataset.getVersions().add(datasetVersion);
                    }
                }
            }
        }

        return dataset;
    }

    @Override
    public String saveDataset(Dataset dataset, Session session) 
            throws PathNotFoundException, RepositoryException {
        
        Node root = session.getNode(metaRootPath);

        Node datasetNode = NodeUtils.addChildIfDoesntExist(root,
                dataset.getName(), MetadataProperty.NLS_DATASET);

        if (dataset.getPath() != null) {
            datasetNode.setProperty(DatasetProperty.PATH, dataset.getPath());
        }

        Node titlesRoot = NodeUtils.addChildIfDoesntExist(datasetNode,
                TITLES_PATH, NodeType.NT_UNSTRUCTURED);
        
        Set<String> languages = dataset.getTranslatedTitles().keySet();
        for (String lang : languages) {
            if (!"".equals(dataset.getTranslatedTitles().get(lang))) {
                Node langNode = NodeUtils.addChildIfDoesntExist(titlesRoot, lang, NodeType.NT_UNSTRUCTURED);
                langNode.setProperty(DatasetProperty.VALUE, dataset.getTranslatedTitles().get(lang));
            }
        }

        NodeIterator existingTitleNodes = titlesRoot.getNodes();
        while (existingTitleNodes.hasNext()) {
            Node titleNode = existingTitleNodes.nextNode();
            if (!languages.contains(titleNode.getName())) {
                titleNode.remove();
            }
        }

        datasetNode.setProperty(MetadataProperty.GMD_FILEIDENTIFIER, dataset.getFileIdentifier());

        datasetNode.setProperty(
                DatasetProperty.SPATIAL_DATASET_IDENTIFIER_CODE,
                dataset.getSpatialDatasetIdentifierCode());

        datasetNode.setProperty(
                DatasetProperty.SPATIAL_DATASET_IDENTIFIER_NAMESPACE,
                dataset.getSpatialDatasetIdentifierNamespace());

        List<SpatialObjectType> spatialObjectTypes = dataset.getSpatialObjectTypes();
        if (spatialObjectTypes.size() > 0) {
            String[] uris = new String[spatialObjectTypes.size()];
            for (int i = 0; i < spatialObjectTypes.size(); i++) {
                uris[i] = spatialObjectTypes.get(i).getUri();
            }
            datasetNode.setProperty(DatasetProperty.SPATIAL_OBJECT_TYPE, uris);
        } else {
            if (datasetNode.hasProperty(DatasetProperty.SPATIAL_OBJECT_TYPE)) {
                datasetNode.getProperty(DatasetProperty.SPATIAL_OBJECT_TYPE).remove();
            }
        }

        datasetNode.setProperty(DatasetProperty.IS_PUBLISHED, dataset.isPublished());

        String license = null;
        if (dataset.getLicence() != null) {
            license = dataset.getLicence().toString();
        } else {
            license = Licence.RESTRICTED.toString();
        }
        datasetNode.setProperty(DatasetProperty.LICENSE, license);
        return datasetNode.getPath();
    }

    @Override
    public DatasetVersion getDatasetVersion(String datasetName,
            String datasetVersionName, Session session)
            throws PathNotFoundException, RepositoryException {

        // let's get corresponding dataset first
        Node datasetNode = session.getNode(metaRootPath + datasetName);
        Dataset dataset = this.getDatasetFromJCRNode(datasetNode, false);
        String datasetVersionPath = VERSIONS_PATH + "/" + datasetVersionName;

        Node datasetVersionNode = datasetNode.getNode(datasetVersionPath);
        DatasetVersion datasetVersion = this
                .getDatasetVersionFromJCRNode(datasetVersionNode);
        datasetVersion.setDataset(dataset);
        dataset.getVersions().add(datasetVersion);
        return datasetVersion;
    }

    @Override
    public void saveDatasetVersion(String datasetName, DatasetVersion datasetVersion, Session session)
            throws PathNotFoundException, RepositoryException {

        // check that containing dataset exists
        Node datasetRoot = session.getNode(metaRootPath);
        if (!datasetRoot.hasNode(datasetName)) {
            throw new PathNotFoundException("Dataset '" + datasetName
                    + "' not found for " + datasetVersion.getName());
        }

        Node datasetNode = datasetRoot.getNode(datasetName);

        Node versionsRoot = NodeUtils.addChildIfDoesntExist(datasetNode,
                VERSIONS_PATH, NodeType.NT_UNSTRUCTURED);
        Node versionNode = NodeUtils.addChildIfDoesntExist(versionsRoot,
                datasetVersion.getName(), NodeType.NT_UNSTRUCTURED);

        versionNode.setProperty(MetadataProperty.NLS_WMSLAYER, datasetVersion.getWmsLayer());
        versionNode.setProperty(MetadataProperty.NLS_WMSMINSCALE, datasetVersion.getWmsMinScale());
        versionNode.setProperty(MetadataProperty.NLS_WMSMAXSCALE, datasetVersion.getWmsMaxScale());

        List<String> formats = datasetVersion.getFormats();
        versionNode.setProperty(MetadataProperty.GMD_DISTRIBUTIONFORMAT, 
                formats.toArray(new String[formats.size()]));

        if (datasetVersion.getLastModified() != null) {
            versionNode.setProperty(MetadataProperty.LASTMODIFIED, datasetVersion.getLastModified());
        }

        Node titlesNode = NodeUtils.addChildIfDoesntExist(versionNode,
                TITLES_PATH, NodeType.NT_UNSTRUCTURED);

        Set<String> languages = datasetVersion.getTranslatedTitles().keySet();
        for (String lang : languages) {
            if (!"".equals(datasetVersion.getTranslatedTitles().get(lang))) {
                Node langNode = NodeUtils.addChildIfDoesntExist(titlesNode, lang, NodeType.NT_UNSTRUCTURED);
                langNode.setProperty(DatasetProperty.VALUE, datasetVersion.getTranslatedTitles().get(lang));
            }
        }

        // remove languages already present but not submitted on save request
        NodeIterator existingLanguageNodes = titlesNode.getNodes();
        while (existingLanguageNodes.hasNext()) {
            Node langNode = existingLanguageNodes.nextNode();
            if (!languages.contains(langNode.getName())) {
                langNode.remove();
            }
        }

        Node crsRoot = NodeUtils.addChildIfDoesntExist(versionNode,
                DatasetProperty.CRS, NodeType.NT_UNSTRUCTURED);

        Set<String> crsIds = new HashSet<String>();
        List<DatasetGridDefinition> griddefs = datasetVersion.getGridDefs();
        if (griddefs != null && griddefs.size() > 0) {
            for (DatasetGridDefinition gd : griddefs) {
                if (gd.getCrs() != null && gd.getCrs().length() > 0) {

                    Node crs = NodeUtils.addChildIfDoesntExist(crsRoot, gd.getCrs().replaceAll(":", "\\$"),
                            NodeType.NT_UNSTRUCTURED);
                    crsIds.add(crs.getName());
                    crs.setProperty(MetadataProperty.NLS_GRIDSCALE, gd.getGridScale()); // TODO always zero?
                    crs.setProperty(DatasetProperty.NLS_GRIDSIZE, gd.getGridSize());
                    /*
                     * if (gd.getGridScale() > 0) {
                     * crs.setProperty(MetadataProperty.NLS_GRIDSYSTEM,
                     * crsMappings.get(gd.getCrs())); }
                     */
                }
            }
        }

        // remove crss already present but not submitted on save request
        NodeIterator existingCrsNodes = crsRoot.getNodes();
        while (existingCrsNodes.hasNext()) {
            Node crsNode = existingCrsNodes.nextNode();
            if (!crsIds.contains(crsNode.getName())) {
                crsNode.remove();
            }
        }
    }

    @Override
    public void deleteDataset(String datasetName, Session session)
            throws AccessDeniedException, PathNotFoundException,
            RepositoryException {
        StringBuilder builder = new StringBuilder(metaRootPath);
        builder.append(datasetName);

        Node dataset = session.getNode(builder.toString());
        dataset.remove();
    }

    @Override
    public void deleteDatasetVersion(String datasetName, String name,
            Session session) throws AccessDeniedException, RepositoryException {
        StringBuilder pathBuilder = new StringBuilder(metaRootPath);
        pathBuilder.append(datasetName);
        pathBuilder.append("/");
        pathBuilder.append(VERSIONS_PATH);
        pathBuilder.append("/");
        pathBuilder.append(name);

        Node versionNode = session.getNode(pathBuilder.toString());
        versionNode.remove();
    }

    @Override
    public Dataset getDatasetById(String datasetName, Session session)
            throws PathNotFoundException, RepositoryException {
        Node datasetNode = session.getNode(metaRootPath + datasetName);
        return getDatasetFromJCRNode(datasetNode);
    }

    @Override
    public NodeIterator queryDatasetFiles(String query, Session session)
            throws InvalidQueryException, RepositoryException {
        QueryManager qm = session.getWorkspace().getQueryManager();
        Query q = qm.createQuery(query, Query.JCR_SQL2);
        QueryResult result = q.execute();
        return result.getNodes();
    }

}
