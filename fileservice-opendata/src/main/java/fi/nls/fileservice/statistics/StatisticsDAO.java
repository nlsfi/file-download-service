package fi.nls.fileservice.statistics;

import java.util.Date;
import java.util.List;

public interface StatisticsDAO {

    public void saveAudit(DownloadStatistic audit);

    public List<DatasetStatistics> getTotalStats();

    public ServiceOrders getOrderCount();

    public List<DailyOrders> getDailyOrders();
    
    public PivotStatistics getDailyStatistics(Date from, Date to);

    public int updateDailyStatistics();
}
