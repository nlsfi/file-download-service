package fi.nls.fileservice.statistics;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;

public class StatisticsServiceImpl implements StatisticsService {

	private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);
	
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
	public PivotStatistics getDailyStatistics(Date from, Date to, Locale locale) {
    	PivotStatistics stats = dao.getDailyStatistics(from, to);
    	String[] datasetIds = stats.getDatasetIds();
    	String[] datasetTitles = new String[datasetIds.length];
    	for (int i=0;i<datasetIds.length;i++) {
    		try {
    			Dataset dataset = datasetService.getDatasetById(datasetIds[i]);
    			String title = dataset.getTranslatedTitles().get(locale.getLanguage());
    			if (title != null) {
    				datasetTitles[i] = title;
    			} else {
    				datasetTitles[i] = datasetIds[i];
    			}
    		} catch (NotFoundException nfe) {
    			datasetTitles[i] = datasetIds[i];
    		}
    	}
    	stats.setDatasetTitles(datasetTitles);
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

    private static void decorateStats(DatasetService datasetService, List<DatasetStatistics> stats, Locale locale) {
        for (DatasetStatistics stat : stats) {
            if (stat.getDatasetId() != null && stat.getDatasetVersionId() != null) {
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
    
    @Override
    @Scheduled(cron="${update.dailystatistics.cron}")
    public void updateDailyStatistics() {
    	try {
    		int result = dao.updateDailyStatistics();
    		logger.info("Updated daily statistics with {} rows", result);
    	} catch (Exception e) {
    		logger.error("Updating daily statistics failed with error: {}", e.getMessage(), e);
    	}
    }
	
}
