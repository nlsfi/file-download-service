package fi.nls.fileservice.statistics;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface StatisticsService {

    public List<DatasetStatistics> getTotalStats(Locale locale);

    public List<DatasetStatistics> getTotalStats(Locale locale, boolean withSummary);
    
    public PivotStatistics getDailyStatistics(Date from, Date to, Locale locale);

    public ServiceOrders getOrderCount();

    public void saveAudit(DownloadStatistic audit);

    public List<DailyOrders> getDailyOrders();
    
    public void updateDailyStatistics();
}
