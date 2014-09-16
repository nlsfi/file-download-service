package fi.nls.fileservice.statistics;

import java.util.List;

public interface StatisticsDAO {

    public void saveAudit(DownloadStatistic audit);

    public List<DatasetStatistics> getTotalStats();

    public ServiceOrders getOrderCount();

    public List<DailyOrders> getDailyOrders();

}
