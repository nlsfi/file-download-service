package fi.nls.fileservice.statistics;

import java.util.Date;

public class DailyOrders {

    private Date day;
    private int count;

    public DailyOrders(Date day, int count) {
        this.day = day;
        this.count = count;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
