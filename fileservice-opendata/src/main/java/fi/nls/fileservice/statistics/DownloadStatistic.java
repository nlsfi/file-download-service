package fi.nls.fileservice.statistics;

public class DownloadStatistic {

    public static enum SERVICE {
        MTP, OPENDATA, INSPIRE, AUTHENTICATED
    };

    private String remoteIP;
    private String uid;
    private String serviceId;
    private String fileIdentifier;
    private String datasetVersion;
    private String path;
    private String format;
    private String crs;
    private long length;
    private long timestamp;

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDatasetID() {
        return fileIdentifier;
    }

    public void setDataset(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getDatasetVersionID() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(remoteIP);
        builder.append(",");
        builder.append(uid);
        builder.append(",");
        builder.append(serviceId);
        builder.append(",");
        builder.append(fileIdentifier);
        builder.append(",");
        builder.append(datasetVersion);
        builder.append(",");
        builder.append(path);
        builder.append(",");
        builder.append(format);
        builder.append(",");
        builder.append(crs);
        builder.append(",");
        builder.append(length);
        builder.append(",");
        builder.append(timestamp);
        return builder.toString();
    }

}
