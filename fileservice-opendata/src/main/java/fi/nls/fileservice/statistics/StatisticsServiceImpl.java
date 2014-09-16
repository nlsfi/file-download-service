package fi.nls.fileservice.statistics;

import java.util.List;
import java.util.Locale;

import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;

public class StatisticsServiceImpl implements StatisticsService {

    private StatisticsDAO dao;
    private DatasetService datasetService;

    public StatisticsServiceImpl(StatisticsDAO dao, DatasetService service) {
        this.dao = dao;
        this.datasetService = service;
    }

    @Override
    public List<DatasetStatistics> getTotalStats(Locale locale) {
        return getTotalStats(locale, true);
    }

    @Override
    public List<DatasetStatistics> getTotalStats(Locale locale,
            boolean withSummary) {
        List<DatasetStatistics> stats = dao.getTotalStats();
        decorateStats(datasetService, stats, locale);

        if (!stats.isEmpty() && withSummary) {
            long totalBytes = 0;
            long totalDownloads = 0;
            for (DatasetStatistics stat : stats) {
                totalBytes += stat.getTotalBytesTransferred();
                totalDownloads += stat.getTotalDownloads();
            }
            DatasetStatistics total = new DatasetStatistics();
            total.setTotalBytesTransferred(totalBytes);
            total.setTotalDownloads(totalDownloads);
            total.setDatasetTitle("Yhteensä"); //TODO remove language hardcode
            stats.add(total);
        }

        return stats;
    }

    @Override
    public List<DailyOrders> getDailyOrders() {
        return dao.getDailyOrders();
    }

    @Override
    public ServiceOrders getOrderCount() {
        return dao.getOrderCount();
    }

    private static void decorateStats(DatasetService datasetService,
            List<DatasetStatistics> stats, Locale locale) {
        for (DatasetStatistics stat : stats) {
            if (stat.getDatasetId() != null
                    && stat.getDatasetVersionId() != null) {
                try {
                    DatasetVersion version = datasetService.getDatasetVersion(stat.getDatasetId(), stat.getDatasetVersionId());
                    stat.setDatasetTitle(version.getDataset().getTranslatedTitles().get(locale.getLanguage()));
                    stat.setDatasetVersionTitle(version.getTranslatedTitles().get(locale.getLanguage()));
                } catch (NotFoundException nfe) {
                    stat.setDatasetTitle(stat.getDatasetId());
                    stat.setDatasetVersionTitle(stat.getDatasetVersionId());
                }
            }
        }
    }

    @Override
    public void saveAudit(DownloadStatistic audit) {
        dao.saveAudit(audit);
    }
}
