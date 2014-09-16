package fi.nls.fileservice.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONOrderTest {

    @Test
    public void testOrderToJSon() throws JsonGenerationException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        OpenDataOrder order = new OpenDataOrder();
        Customer customer = new Customer();
        customer.setEmail("foo@example.com");
        order.setCustomer(customer);

        order.getFiles().add("path/to/file");
        order.getFiles().add("path/to/file1");

        Writer strWriter = new StringWriter();
        mapper.writeValue(strWriter, order);
        strWriter.toString();
        // except no errors..
    }

    @Test
    public void testJsonToOrder() throws JsonGenerationException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        OpenDataOrder order = mapper.readValue(new File(
                "target/test-classes/opendataorder.json"), OpenDataOrder.class);
        assertNotNull(order.getCustomer());
        assertEquals("erkki.esimerkki@firma.fi", order.getCustomer().getEmail());
        assertEquals("Erkki", order.getCustomer().getFirstName());
        assertEquals("Esimerkki", order.getCustomer().getLastName());
        assertTrue(order.getCustomer().isLicenceAccepted());
        assertNull(order.getCustomer().getOrganisation());
        List<String> files = order.getFiles();
        assertEquals(2, files.size());
        assertTrue(files
                .contains("/tuotteet/nimisto/karttanimet_250/etrs89/gml/karttanimet_250_2012_02.zip"));
        assertTrue(files
                .contains("/tuotteet/nimisto/paikat/etrs89/gml/paikat_2012_02.zip"));
    }

    @Test
    public void testJsonToOrder2() throws JsonGenerationException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        OpenDataOrder order = mapper
                .readValue(new File("target/test-classes/opendataorder2.json"),
                        OpenDataOrder.class);
        assertNotNull(order.getCustomer());
        assertEquals("erkki.esimerkki@firma.fi", order.getCustomer().getEmail());
        assertNull(order.getCustomer().getFirstName());
        assertNull(order.getCustomer().getLastName());
        assertEquals("Erkki & Matti Oy", order.getCustomer().getOrganisation());
        List<String> files = order.getFiles();
        assertEquals(2, files.size());
        assertTrue(files
                .contains("/tuotteet/nimisto/karttanimet_250/etrs89/gml/karttanimet_250_2012_02.zip"));
        assertTrue(files
                .contains("/tuotteet/nimisto/paikannimet_kaikki/etrs89/gml/paikannimet_2012_02.zip"));
    }
}
