package fi.nls.fileservice.dataset;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatasetJsonTest {

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void export2Json() throws JsonGenerationException,
            JsonMappingException, IOException {

        List<Dataset> datasets = new ArrayList<Dataset>();
        Dataset dataset = new Dataset();
        dataset.setFileIdentifier("0e55977c-00c9-4c46-9c87-dee6b27d2d5c");

        DatasetVersion dv = new DatasetVersion();
        dv.getFormats().add("XML/GML");
        dv.getFormats().add("TXT");

        dataset.getVersions().add(dv);

        datasets.add(dataset);

        Writer strWriter = new StringWriter();
        mapper.writeValue(strWriter, datasets);
        strWriter.toString();

    }
}
