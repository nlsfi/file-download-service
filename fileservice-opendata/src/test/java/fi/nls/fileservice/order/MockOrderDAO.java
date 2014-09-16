package fi.nls.fileservice.order;

import java.util.List;

import fi.nls.fileservice.common.DataAccessException;

public class MockOrderDAO implements OrderDAO {

    private List<MtpCustomer> orders;

    public MockOrderDAO(List<MtpCustomer> orders) {
        this.orders = orders;
    }

    @Override
    public MtpCustomer getMtpCustomer(String email) throws DataAccessException {
        for (MtpCustomer order : orders) {
            if (order.getEmail().equals(email)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public void saveMtpOrder(MtpCustomer user) throws DataAccessException {
        MtpCustomer existing = getMtpCustomer(user.getEmail());
        if (existing != null) {
            throw new IllegalStateException("Mtp key already esixts for : "
                    + user.getEmail());
        }
        orders.add(user);
    }

    @Override
    public void saveOpenDataOrder(String token, OpenDataOrder order) {
        // TODO Auto-generated method stub

    }

    @Override
    public OpenDataOrder getOpenDataOrder(String token) {
        // TODO Auto-generated method stub
        return null;
    }

}
