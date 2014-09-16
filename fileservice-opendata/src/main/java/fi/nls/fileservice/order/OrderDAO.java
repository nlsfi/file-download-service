package fi.nls.fileservice.order;

import fi.nls.fileservice.common.DataAccessException;

public interface OrderDAO {

    public MtpCustomer getMtpCustomer(String email) throws DataAccessException;

    public void saveMtpOrder(MtpCustomer customer) throws DataAccessException;

    public void saveOpenDataOrder(String token, OpenDataOrder order);

    public OpenDataOrder getOpenDataOrder(String token);
}
