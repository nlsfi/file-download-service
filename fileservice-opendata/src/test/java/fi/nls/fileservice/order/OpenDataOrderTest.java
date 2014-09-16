package fi.nls.fileservice.order;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OpenDataOrderTest {

    @Test
    public void testPathValidation() {
        OpenDataOrder order = new OpenDataOrder();
        order.getFiles().add("\\invalid\\path\\");

        assertFalse(order.isValid());

    }

    @Test
    public void testBadPathValidation() {
        OpenDataOrder order = new OpenDataOrder();
        order.getFiles().add("sdfsdfsdf");
        assertFalse(order.isValid());
    }

    @Test
    public void testBadPathValidation2() {
        OpenDataOrder order = new OpenDataOrder();
        order.getFiles().add("/foo/bar;/2134.zip");
        assertFalse(order.isValid());
    }

    @Test
    public void testPathValidationOk() {
        OpenDataOrder order = new OpenDataOrder();
        order.getFiles().add("/foo/bar/zed/1234.zip");

        assertTrue(order.isValid());

    }

}
