package fi.nls.fileservice.statistics;

import java.util.HashMap;
import java.util.Map;

public class ServiceOrders {

    public static final String SERVICE_MTP = "service.mtp";
    public static final String SERVICE_OPENDATA_MAP = "service.map";
    public static final String SERVICE_FILE_BROWSER = "service.tiedosto";
    public static final String SERVICE_INSPIRE = "service.inspire";

    private Map<String, Integer> orderCount = new HashMap<String, Integer>();

    private int totalCustomers;

    public void addCount(String serviceID, int count) {
        this.orderCount.put(serviceID, count);
    }

    public Map<String, Integer> getOrderCount() {
        return this.orderCount;
    }

    public void setTotalCustomers(int num) {
        this.totalCustomers = num;
    }

    public int getTotalCustomers() {
        return this.totalCustomers;
    }

}
