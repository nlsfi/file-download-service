package fi.nls.fileservice.statistics;

import java.util.Date;
import java.util.List;

public class PivotStatistics {

    private String[] datasetIds;
    private String[] datasetTitles;
    private List<List<DatasetStatistics>> dailyStats;
    private List<Date> days;

    public PivotStatistics(String[] datasetIds,
            List<List<DatasetStatistics>> dailyStats, List<Date> days) {
        this.datasetIds = datasetIds;
        this.dailyStats = dailyStats;
        this.days = days;
    }

    public String[] getDatasetIds() {
        return datasetIds;
    }

    public void setDatasetIds(String[] datasetIds) {
        this.datasetIds = datasetIds;
    }

    public String[] getDatasetTitles() {
        return datasetTitles;
    }

    public void setDatasetTitles(String[] datasetTitles) {
        this.datasetTitles = datasetTitles;
    }

    public List<List<DatasetStatistics>> getDailyStats() {
        return dailyStats;
    }

    public void setDailyStats(List<List<DatasetStatistics>> dailyStats) {
        this.dailyStats = dailyStats;
    }

    public List<Date> getDays() {
        return days;
    }

    public void setDays(List<Date> days) {
        this.days = days;
    }

}
