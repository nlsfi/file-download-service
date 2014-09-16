package fi.nls.fileservice.order;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.mail.MailService;
import fi.nls.fileservice.util.SecureRandomTokenGenerator;

public class OrderServiceTest {

    // private OrderDAO dao;
    // private OrderService orderService;
    private MailService service;

    @Before
    public void setUp() {

        List<MtpCustomer> orders = new ArrayList<MtpCustomer>();

        MtpCustomer a = new MtpCustomer();
        a.setEmail("john.doe@company.com");
        orders.add(a);

        MtpCustomer b = new MtpCustomer();
        b.setEmail("jane.doe@anothercompany.com");
        orders.add(b);

        // dao = new MockOrderDAO(orders);
        // TokenGenerator generator = new SecureRandomTokenGenerator();

        this.service = mock(MailService.class);
        // OrderService failService = new OrderServiceImpl(dao,new
        // SecureRandomTokenGenerator(), service);

        // orderService = new OrderServiceImpl(dao, service,
        // null,null,null,null,generator,null,null);

    }

    @Test
    /*
     * public void doTestSaveOrder() { MtpUser newUser = new MtpUser();
     * newUser.setEmail("mikko.mittari@example.com");
     * orderService.storeMtpOrder(newUser);
     * 
     * newUser = new MtpUser(); newUser.setEmail("mikko.mittari@example.com");
     * newUser = dao.getMtpUser(newUser); assertNotNull(newUser);
     * assertNotNull(newUser.getApiKey()); assertNull(newUser.getFirstname());
     * assertNull(newUser.getLastname()); assertNull(newUser.getOrganization());
     * assertEquals("mikko.mittari@example.com", newUser.getEma());
     * 
     * }
     */
    public void doTestIOError() {
        OrderDAO dao = mock(OrderDAO.class);
        when(dao.getMtpCustomer((String) anyObject())).thenThrow(
                new DataAccessException("error"));
        OrderService failService = new OrderServiceImpl(dao, service, null,
                null, null, null, new SecureRandomTokenGenerator(), null, null);

        try {
            MtpCustomer user = new MtpCustomer();
            user.setEmail("should.fail@error.com");
            failService.saveMtpOrder(user, new Locale("fi", "FI"));
            fail("OrderService should have thrown DataAccessException");
        } catch (Exception e) {
            // expected
        }
    }
}
