package fi.nls.fileservice.statistics.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.security.pgsql.AccessTokenType;
import fi.nls.fileservice.statistics.DailyOrders;
import fi.nls.fileservice.statistics.DatasetStatistics;
import fi.nls.fileservice.statistics.DownloadStatistic;
import fi.nls.fileservice.statistics.PivotStatistics;
import fi.nls.fileservice.statistics.ServiceOrders;
import fi.nls.fileservice.statistics.StatisticsDAO;

public class PGStatisticsDAO implements StatisticsDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(PGStatisticsDAO.class);

	private static final String INSERT_QUERY = "INSERT INTO download_statistic(remoteip,service,username,dataset,dataset_version,jcr_path,format,crs,bytes,downloaded_at) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?)";

	private final DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public PGStatisticsDAO(DataSource dataSource, JdbcTemplate jdbcTemplate) {
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void saveAudit(DownloadStatistic audit) {

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();

			stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setString(1, audit.getRemoteIP());
			stmt.setString(2, audit.getServiceId());
			stmt.setString(3, audit.getUid());
			stmt.setString(4, audit.getDatasetID());
			stmt.setString(5, audit.getDatasetVersionID());
			stmt.setString(6, audit.getPath());
			stmt.setString(7, audit.getFormat());
			stmt.setString(8, audit.getCrs());
			stmt.setLong(9, audit.getLength());
			stmt.setTimestamp(10, new Timestamp(audit.getTimestamp()));

			stmt.executeUpdate();

		} catch (SQLException sqle) {
			// log error and continue, we will not fail if only
			// recording of download statistics fail
			logger.error("Error saving audit data {" + audit.toString() + "}",
					sqle);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.warn("Error closing statement", e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.warn("Error closing connection", e);
				}
			}
		}

	}

	@Override
	public List<DatasetStatistics> getTotalStats() {

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			con = dataSource.getConnection();
			statement = con
					.prepareStatement("SELECT dataset, dataset_version, sum(files_count), sum(bytes_total) FROM daily_download_statistic "
							+ "WHERE dataset IS NOT NULL GROUP BY dataset,dataset_version");

			resultSet = statement.executeQuery();

			List<DatasetStatistics> statsList = new ArrayList<DatasetStatistics>();

			while (resultSet.next()) {
				DatasetStatistics stats = new DatasetStatistics();
				stats.setDatasetId(resultSet.getString(1));
				stats.setDatasetVersionId(resultSet.getString(2));
				stats.setTotalDownloads(resultSet.getLong(3));
				stats.setTotalBytesTransferred(resultSet.getLong(4));
				statsList.add(stats);
			}
			return statsList;
		} catch (SQLException sqle) {
			throw new DataAccessException(sqle);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
		}
	}

	@Override
	public PivotStatistics getDailyStatistics(Date from, Date to) {
		
		Connection con = null;
		PreparedStatement smnt = null; 
		ResultSet resultSet = null;
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Date> days = new ArrayList<Date>();
		List<List<DatasetStatistics>> dailyStats = new ArrayList<List<DatasetStatistics>>();
		List<DatasetStatistics> datasetStats = null;
		
		Set<String> datasets = new HashSet<String>();
		
		try {
			con = dataSource.getConnection();
			smnt = con.prepareStatement("SELECT day, dataset, count(*), sum(bytes_total) FROM daily_download_statistic GROUP BY day,dataset ORDER BY day");
			resultSet = smnt.executeQuery();
			
			java.util.Date currentDay = null;
			while(resultSet.next()) {

				// assume results are ordered by day
				java.sql.Date day = resultSet.getDate(1);
				if (currentDay == null || day.after(currentDay)) {
					currentDay = day;
					days.add(currentDay);
					datasetStats = new ArrayList<DatasetStatistics>();
					dailyStats.add(datasetStats);
				}

				DatasetStatistics stat = new DatasetStatistics();
				stat.setDatasetId(resultSet.getString(2));
				stat.setTotalDownloads(resultSet.getLong(3));
				stat.setTotalBytesTransferred(resultSet.getLong(4));

				datasetStats.add(stat);
				datasets.add(stat.getDatasetId());
			}
			
			String[] columnNames = datasets.toArray(new String[datasets.size()]);
			PivotStatistics pivot = new PivotStatistics(columnNames, dailyStats, days);
			return pivot;
		} catch (SQLException sqle) {
			logger.warn("Error querying statistics", sqle);
			throw new DataAccessException(sqle);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
			if (smnt != null) {
				try {
					smnt.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.warn("Error closing jdbc resources", e);
				}
			}
		}
	}

	@Override
	public List<DailyOrders> getDailyOrders() {
		return jdbcTemplate
				.query("SELECT date_trunc('day',created), count(*) FROM open_data_order GROUP BY date_trunc('day',created)",
						new RowMapper<DailyOrders>() {

							@Override
							public DailyOrders mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return new DailyOrders(rs.getDate(1), rs
										.getInt(2));
							}

						});
	}

	@Override
	public ServiceOrders getOrderCount() {
		int mtpOrders = jdbcTemplate.queryForObject(
				"SELECT count(*) FROM access_token WHERE token_type = ?",
				Integer.class, AccessTokenType.APIKEY_TYPE);

		int openDataOrders = jdbcTemplate.queryForObject(
				"SELECT count(*) FROM access_token WHERE token_type = ?",
				Integer.class, AccessTokenType.TOKEN_TYPE);

		int totalCustomers = jdbcTemplate.queryForObject(
				"SELECT count(distinct email) FROM customer", Integer.class);

		ServiceOrders orders = new ServiceOrders();
		orders.addCount(ServiceOrders.SERVICE_MTP, mtpOrders);
		orders.addCount(ServiceOrders.SERVICE_OPENDATA_MAP, openDataOrders);
		orders.addCount("service.customers", totalCustomers);
		orders.setTotalCustomers(totalCustomers);
		return orders;
	}
	
	@Override
	public int updateDailyStatistics() {
		return jdbcTemplate.update("INSERT INTO daily_download_statistic(day,dataset,dataset_version,files_count,bytes_total) SELECT date_trunc('day',downloaded_at),dataset,dataset_version,count(*),sum(bytes) FROM download_statistic WHERE date_trunc('day', downloaded_at) > (SELECT COALESCE(max(day),'1970-01-01') from daily_download_statistic) AND date_trunc('day', downloaded_at) < date_trunc('day', current_timestamp) AND dataset IS NOT NULL GROUP by date_trunc('day',downloaded_at),dataset,dataset_version"); 
	}

}
