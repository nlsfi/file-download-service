package fi.nls.fileservice.web.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.web.common.UriComponentsLinkBuilder;

public class UriComponentsLinkBuilderTest {

    @Test
    public void testWithParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", "abcdefghijklm");

        UriComponentsLinkBuilder builder = new UriComponentsLinkBuilder(
                UriComponentsBuilder.fromHttpUrl(
                        "http://test.example.com/service").build(), params,
                "api");

        String uri = builder.buildUri("/test/file");
        assertEquals(
                "http://test.example.com/service/api/test/file?api_key=abcdefghijklm",
                uri);

    }

    @Test
    public void testWithMultiplePathSegments() {
        UriComponentsLinkBuilder builder = new UriComponentsLinkBuilder(
                UriComponentsBuilder.fromHttpUrl(
                        "http://test.example.com/service").build(), null,
                "api", "v2");

        String uri = builder.buildUri("/test/file");
        assertEquals("http://test.example.com/service/api/v2/test/file", uri);
    }

}
