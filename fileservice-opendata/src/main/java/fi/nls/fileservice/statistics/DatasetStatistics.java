package fi.nls.fileservice.statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.nls.fileservice.util.Formatter;

public class DatasetStatistics {

    private String day;
    private String datasetId;
    private String datasetVersionId;

    private String datasetTitle;
    private String datasetVersionTitle;

    private long totalBytesTransferred;
    private long totalDownloads;

    public Date getDay() {
        if (day != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(day);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getDayStr() {
        return day;
    }

    public void setDayStr(String day) {
        this.day = day;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetVersionId() {
        return datasetVersionId;
    }

    public void setDatasetVersionId(String datasetVersionId) {
        this.datasetVersionId = datasetVersionId;
    }

    public String getDatasetTitle() {
        return datasetTitle;
    }

    public void setDatasetTitle(String datasetTitle) {
        this.datasetTitle = datasetTitle;
    }

    public String getDatasetVersionTitle() {
        return datasetVersionTitle;
    }

    public void setDatasetVersionTitle(String datasetVersionTitle) {
        this.datasetVersionTitle = datasetVersionTitle;
    }

    public long getTotalBytesTransferred() {
        return totalBytesTransferred;
    }

    public void setTotalBytesTransferred(long totalBytesTransferred) {
        this.totalBytesTransferred = totalBytesTransferred;
    }

    public long getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(long totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public String getFormattedBytes() {
        return Formatter.formatLength(totalBytesTransferred);
    }

}
