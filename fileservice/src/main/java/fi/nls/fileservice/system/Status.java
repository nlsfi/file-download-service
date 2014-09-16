package fi.nls.fileservice.system;

import java.util.Map;

import fi.nls.fileservice.util.Formatter;

public class Status {

    private int availableProcessors;

    private String dbServerVersion;
    private String jdbcError;
    private boolean isDbServerOk;

    private long freeMemory;
    private long maxMemory;
    private long totalMemory;
    private String javaVendor;
    private String javaVersion;

    private int threadCount;
    private int peakThreadCount;
    private int daemonThreadCount;

    private boolean isModeShapeOk;
    private String modeShapeError;

    private String osName;
    private String osArch;
    private String osVersion;

    private Map<String, String> env;

    public Status() {

    }

    public boolean isModeShapeOk() {
        return isModeShapeOk;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public void setModeShapeOk(boolean isModeShapeOk) {
        this.isModeShapeOk = isModeShapeOk;
    }

    public String getModeShapeError() {
        return modeShapeError;
    }

    public void setModeShapeError(String modeShapeError) {
        this.modeShapeError = modeShapeError;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public boolean isDbServerOk() {
        return isDbServerOk;
    }

    public void setDbServerOk(boolean isDbServerOk) {
        this.isDbServerOk = isDbServerOk;
    }

    public String getDbServerVersion() {
        return dbServerVersion;
    }

    public void setDbServerVersion(String dbServerVersion) {
        this.dbServerVersion = dbServerVersion;
    }

    public String getJdbcError() {
        return jdbcError;
    }

    public void setJDBCError(String jdbcError) {
        this.jdbcError = jdbcError;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public String getTotalMemoryStr() {
        return Formatter.formatLength(totalMemory);
    }

    public String getFreeMemoryStr() {
        return Formatter.formatLength(freeMemory);
    }

    public String getMaxMemoryStr() {
        return Formatter.formatLength(maxMemory);
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public boolean isStatusOk() {
        return isModeShapeOk && isDbServerOk;
    }

}
