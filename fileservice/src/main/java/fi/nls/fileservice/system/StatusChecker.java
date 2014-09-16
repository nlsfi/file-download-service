package fi.nls.fileservice.system;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.sql.DataSource;

import fi.nls.fileservice.security.jcr.CredentialsProvider;

public class StatusChecker {

    private DataSource dataSource;
    private Repository repository;
    private CredentialsProvider credentialsProvider;

    public StatusChecker() {

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    protected void checkModeShape(Status status) {
        Session session = null;
        try {
            session = repository.login(credentialsProvider.getCredentials());
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery("SELECT * FROM [nt:file] LIMIT 1",
                    Query.JCR_SQL2);
            QueryResult result = query.execute();
            RowIterator iter = result.getRows();
            if (iter.hasNext()) {
                iter.nextRow();
            }
            status.setModeShapeOk(true);
        } catch (Exception e) {
            e.printStackTrace();
            status.setModeShapeOk(false);
            status.setModeShapeError(e.getMessage());
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    protected void checkJVM(Status status) {
        status.setJavaVendor(System.getProperty("java.vendor"));
        status.setJavaVersion(System.getProperty("java.version"));
        status.setOsName(System.getProperty("os.name"));
        status.setOsArch(System.getProperty("os.arch"));
        status.setOsVersion(System.getProperty("os.version"));
        status.setAvailableProcessors(Runtime.getRuntime()
                .availableProcessors());
        status.setMaxMemory(Runtime.getRuntime().maxMemory());
        status.setFreeMemory(Runtime.getRuntime().freeMemory());
        status.setTotalMemory(Runtime.getRuntime().totalMemory());
        status.setEnv(System.getenv());

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        status.setPeakThreadCount(bean.getPeakThreadCount());
        status.setDaemonThreadCount(bean.getDaemonThreadCount());
        status.setThreadCount(bean.getThreadCount());

    }

    protected void checkJDBCDataSource(Status status) {

        status.setDbServerOk(true);
        Connection con = null;
        PreparedStatement smnt = null;
        ResultSet set = null;
        try {
            con = dataSource.getConnection();
            DatabaseMetaData data = con.getMetaData();
            String query = null;
            if (data.getDriverName() != null && data.getDriverName().contains("HSQL")) {
                query = "SELECT 1";
            } else {
                // PostgreSQL and MySQL/MariaDB compatible query
                query = "SELECT VERSION()";
            }
            smnt = con.prepareStatement(query);
            set = smnt.executeQuery();
            if (set.next()) {
                status.setDbServerVersion(set.getString(1));
                status.setDbServerOk(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            status.setDbServerOk(false);
            status.setJDBCError(e.getMessage());
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (Exception e) {
                }
            }
            if (smnt != null) {
                try {
                    smnt.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }

    }

    public Status getStatus() {
        Status status = new Status();
        checkJVM(status);
        checkJDBCDataSource(status);
        checkModeShape(status);
        return status;
    }
}
