package fi.nls.fileservice.files;

/**
 * Encapsulates JCR query parameters and transforms them to JCR-SQL2 queries
 * 
 */
public class DatasetQueryParams {

    private String path;
    private String dataset;
    private String fileIdentifier;
    private String datasetVersion;
    private String distributionFormat;
    private String crs;
    private String[] gridIds;

    private String updated;
    private int offset;
    private int limit;

    private String orderBy;

    public boolean isOffsetQuery() {
        return offset > 0 || limit > 0;
    }

    public int getNextOffset() {
        return offset + limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public String getDistributionFormat() {
        return distributionFormat;
    }

    public void setDistributionFormat(String distributionFormat) {
        this.distributionFormat = distributionFormat;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String[] getGridIds() {
        return gridIds;
    }

    public void setGridIds(String[] gridIds) {
        this.gridIds = gridIds;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getQueryType() {
        return javax.jcr.query.Query.JCR_SQL2;
    }

    public String getQuery() {
        StringBuilder querySb = new StringBuilder("SELECT  file.*");
        querySb.append(" FROM [nls:datasetfile] AS file");
        querySb.append(" WHERE file.[nls:dataset] = '");
        querySb.append(getDataset());
        querySb.append("'");
        if (getDatasetVersion() != null) {
            querySb.append(" AND file.[nls:datasetVersion] = '");
            querySb.append(getDatasetVersion());
            querySb.append("'");
        }
        if (getDistributionFormat() != null) {
            querySb.append(" AND file.[gmd:distributionFormat] = '");
            querySb.append(getDistributionFormat());
            querySb.append("'");
        }
        if (getCrs() != null) {
            querySb.append(" AND file.[nls:crs] = '");
            querySb.append(getCrs());
            querySb.append("'");
        }

        String[] gridIds = getGridIds();
        if (gridIds != null && gridIds.length > 0) {
            querySb.append(" AND file.[nls:gridCell] IN ('");
            querySb.append(gridIds[0]);
            querySb.append("'");
            for (int i = 1; i < gridIds.length; i++) {
                querySb.append(",'");
                querySb.append(gridIds[i]);
                querySb.append("'");
            }
            querySb.append(")");

        }

        if (updated != null) {
            // querySb.append(" AND file.[jcr:created] >= CAST('");
            querySb.append(" AND file.[nls:fileChanged] >= CAST('");
            querySb.append(updated);
            querySb.append("' AS DATE)");
        }

        if (orderBy != null) {
            querySb.append(" ORDER BY file.[");
            querySb.append(orderBy);
            querySb.append("] DESC");
        }

        if (limit > 0) {
            querySb.append(" LIMIT ");
            querySb.append(Integer.toString(limit));
        }

        if (offset > 0) {
            querySb.append(" OFFSET ");
            querySb.append(Integer.toString(offset));
        }

        return querySb.toString();
    }

}
