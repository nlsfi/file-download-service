package fi.nls.fileservice.mail;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import fi.nls.fileservice.mail.impl.FreemarkerTemplateResolver;
import freemarker.template.Configuration;

public class FreemarkerTemplateResolverTest {

    private static FreemarkerTemplateResolver templateResolver;

    @BeforeClass
    public static void setUp() throws Exception {
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setPreferFileSystemAccess(false);
        factory.setTemplateLoaderPath("classpath:");
        factory.setDefaultEncoding("UTF-8");
        Configuration configuration = factory.createConfiguration();
        templateResolver = new FreemarkerTemplateResolver(configuration);

    }

    @Test
    public void testOrderMail() {
        String email = "john.doe@example.com";
        String uri = "https://tiedostopalvelu.maanmittauslaitos.fi/tp/tilaus/xxxxxx";

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("email", email);
        model.put("uri", uri);
        model.put("date", new Date());

        String message = templateResolver.getMessage("mail/tilaus_sp_fi.ftl",
                model);

        assertTrue(message.contains(email));
        assertTrue(message.contains(uri));
        assertFalse(message.contains("${"));
        assertFalse(message.contains("}"));

    }

}
