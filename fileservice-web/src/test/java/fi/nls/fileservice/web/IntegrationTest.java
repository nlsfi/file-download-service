package fi.nls.fileservice.web;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.Privilege;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebConfig.class })
@WebAppConfiguration
public class IntegrationTest {

    private MockMvc mvc;

    @Autowired
    WebApplicationContext wac;
    
    @Autowired
    DatasetService datasetService;
   
    @Autowired
    private AccessPolicyManager apm;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private FileService fileService;

    @BeforeClass
    public static void setUp() throws IllegalStateException, NamingException {
        // Register in mock JNDI
        // an embedded HSQLDB with PostgreSQL syntax enabled
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .setName("tiepaldb;sql.syntax_pgs=true")
                .addScript("file:../resources/sql/create-tables.sql")
                .addScript("file:../resources/sql/data-hsql.sql")
                .build();

        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/jdbc/tiepaldb", dataSource);
        builder.activate();

    }
    
    @Before
    public void createMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
	public void testStatus() throws Exception {
       
        mvc.perform(get("/hallinta/status")).andExpect(status().isOk());
		//FileService service = config.fileService();
		//DetachedNode rootNode = service.getNode("/");
		
	}
    
    @Test
    public void testSubmitMtpForm() throws Exception {
        mvc.perform(post("/mtp/tilaus").param("email", "john.doe@example.com").
                param("_licenseAccepted", "on")
                .param("licenceAccepted", "true")
                .param("lang", "fi"))
            .andExpect(status().isFound()) //FIXME according to HTTP RFC this status code is not necessarily correct..
            .andExpect(redirectedUrl("/mtp/tilaus_ok"));
    }
    
    @Test
    public void testSaveDataset() {
        Dataset dataset = new Dataset();
        dataset.setName("maastotietokanta");
        dataset.setPath("/tuotteet/maastotietokanta");
        dataset.setLicence(Licence.OPENDATA);

        datasetService.saveDataset(dataset);
        dataset = datasetService.getDatasetById(dataset.getName());
        AccessPolicy policy = apm.getAccessPolicy(env.getProperty("opendata.account.name"));
       
        assertTrue(policy.isAllowed(dataset.getPath() + "/kaikki.zip", Privilege.READ.getName()));
        assertTrue(policy.isAllowed(dataset.getMetadataPath(), Privilege.READ.getName()));
    }
    
    @Test
    public void testCreateAndDeleteFile() {
        byte[] data = "test\r\n".getBytes();
        String path = fileService.saveFile("/tuotteet", "testi.txt", new ByteArrayInputStream(data));
        fileService.delete(path);
        
    }

}
