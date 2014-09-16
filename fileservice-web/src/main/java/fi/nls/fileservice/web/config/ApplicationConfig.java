package fi.nls.fileservice.web.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.jcr.Credentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.dataset.jcr.DatasetServiceImpl;
import fi.nls.fileservice.dataset.jcr.JCRDatasetDAO;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.files.FileServiceImpl;
import fi.nls.fileservice.files.PropertiesFilter;
import fi.nls.fileservice.jcr.service.ModeShapeRepositoryService;
import fi.nls.fileservice.jcr.service.RepositoryService;
import fi.nls.fileservice.mail.MailService;
import fi.nls.fileservice.mail.impl.FreemarkerTemplateResolver;
import fi.nls.fileservice.mail.impl.SpringMailServiceImpl;
import fi.nls.fileservice.order.OrderService;
import fi.nls.fileservice.order.OrderServiceImpl;
import fi.nls.fileservice.order.pgsql.PGOrderDAO;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.jcr.CredentialsProvider;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;
import fi.nls.fileservice.security.jcr.SystemCredentialsProvider;
import fi.nls.fileservice.security.pgsql.PGAccessPolicyManager;
import fi.nls.fileservice.security.service.DummyUserAttributesService;
import fi.nls.fileservice.security.service.UserService;
import fi.nls.fileservice.security.service.UserServiceImpl;
import fi.nls.fileservice.security.service.UserAttributesProvider;
import fi.nls.fileservice.security.service.ldap.LDAPUserAttributesProvider;
import fi.nls.fileservice.statistics.StatisticsService;
import fi.nls.fileservice.statistics.StatisticsServiceImpl;
import fi.nls.fileservice.statistics.pgsql.PGStatisticsDAO;
import fi.nls.fileservice.util.SecureRandomTokenGenerator;
import fi.nls.fileservice.util.TokenGenerator;
import fi.nls.fileservice.web.feed.atom.builder.FeedMetadata;
import freemarker.template.TemplateException;

@Configuration
// @EnableScheduling //DISABLED for the moment
@ComponentScan(basePackages = "fi.nls.fileservice")
@PropertySource(value = { "classpath:/config.properties", "classpath:/tp-config-ext.properties" })
@Import(DataSourceConfig.class)
@ImportResource({ "classpath:mappings.xml" })
public class ApplicationConfig {

    @Autowired
    private Environment env;

    @Autowired
    private DataSourceConfig dataSourceConfig;
    
    @Resource(name = "crsDefinitions")
    private Map<String, CrsDefinition> crsDefinitions;

    @Bean
    public CredentialsProvider systemCredentialsProvider() {
        Credentials credentialsProvider = new ExternalAuthenticationCredentials(
                env.getProperty("system.account.name"), null);
        return new SystemCredentialsProvider(credentialsProvider);
    }

    @Bean
    public AccessPolicyManager accessPolicyManager() {
        PGAccessPolicyManager pm = new PGAccessPolicyManager();
        pm.setDataSource(dataSourceConfig.orderDataSource());
        return pm;
    }

    @Bean
    public DatasetDAO datasetDAO() {
        return new JCRDatasetDAO(env.getProperty("datasets.jcr.path"), crsDefinitions);
    }

    @Bean
    public DatasetService datasetService() {
        DatasetServiceImpl service = new DatasetServiceImpl(datasetDAO(),
                dataSourceConfig.repository(), accessPolicyManager(),
                env.getProperty("opendata.account.name", String.class));
        return service;
    }

    @Bean
    public FileService fileService() {
        FileService service = new FileServiceImpl(dataSourceConfig.repository());
        return service;
    }

    @Bean
    public TokenGenerator tokenGenerator() {
        return new SecureRandomTokenGenerator();
    }

    @Bean
    public PropertiesFilter propertyFilter() {
        String propertyList = env.getProperty("customer.ui.display.properties");
        if (propertyList != null) {
            String[] allowedPropertyNames = propertyList.split(",");
            Arrays.sort(allowedPropertyNames);
            return new PropertiesFilter(allowedPropertyNames);
        } else {
            return new PropertiesFilter(new String[0]);
        }
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames("/WEB-INF/messages/messages", "classpath:messages");
        // must use ISO-8869-1 encoding for property files
        // see:
        // http://docs.oracle.com/javase/6/docs/api/java/util/Properties.html
        ms.setDefaultEncoding("iso-8859-1");
        ms.setCacheSeconds(0);
        return ms;
    }

    @Bean
    public FeedMetadata feedMetadata() {
        FeedMetadata metadata = new FeedMetadata();
        metadata.setMetadataUri(env.getProperty("feed.metadata.uri"));
        metadata.setMetadataHTMLUri(env.getProperty("feed.metadata.htmluri"));
        metadata.setMetadataMimeType(env.getProperty("inspire_atom_feed_csw_mimetype"));
        metadata.setMetadataHTMLMimeType("text/html");
        metadata.setServiceDescriptionFileIdentifier(env.getProperty("feed.inspire.fileidentifier"));

        String languageStr = env.getProperty("feed.languages");
        if (languageStr != null) {
            String[] languages = languageStr.split(",");
            metadata.setLanguages(languages);
        }
        return metadata;
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setPreferFileSystemAccess(false);
        factory.setTemplateLoaderPath("classpath:");
        factory.setDefaultEncoding("UTF-8");
        try {
            freemarker.template.Configuration config = factory
                    .createConfiguration();
            config.setEncoding(new java.util.Locale("fi", "FI"), "UTF-8"); // FIXME remove hardcode
            return config;
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public OrderService orderService() {
        PGOrderDAO dao = new PGOrderDAO();
        dao.setDataSource(dataSourceConfig.orderDataSource());
        dao.setOrderValidDays(env.getProperty("token.valid.days", Integer.class));
        return new OrderServiceImpl(dao, mailService(), fileService(),
                datasetDAO(), propertyFilter(), dataSourceConfig.repository(),
                new SecureRandomTokenGenerator(),
                new FreemarkerTemplateResolver(freemarkerConfiguration()),
                messageSource());
    }

    // DISABLED for the moment
    /*
     * @Bean public RepositoryJanitor repositoryJanitor() { RepositoryJanitor
     * janitor = new RepositoryJanitor(repository(),
     * systemCredentialsProvider()); return janitor; }
     */

    @Bean
    public StatisticsService statisticsService() {
        JdbcTemplate template = new JdbcTemplate(dataSourceConfig.orderDataSource());

        PGStatisticsDAO statisticsDAO = new PGStatisticsDAO(
                dataSourceConfig.orderDataSource(), template);

        StatisticsService service = new StatisticsServiceImpl(statisticsDAO,
                datasetService());
        
        return service;
    }

    @Bean
    public UserService permissionsService() {

        UserAttributesProvider uas;
        if (env.containsProperty("java.naming.provider.url")) {

            LDAPUserAttributesProvider luas = new LDAPUserAttributesProvider();
            luas.setProviderUrl(env.getProperty("java.naming.provider.url"));
            luas.setSecurityProtocol(env.getProperty("java.naming.security.protocol"));
            luas.setPrincipal(env.getProperty("java.naming.security.principal"));
            luas.setCredentials(env.getProperty("java.naming.security.credentials"));
            luas.setSearchFilter(env.getProperty("ldap.search.filter"));
            luas.setSearchBase(env.getProperty("ldap.search.base"));

            String returnAttrStr = env.getProperty("ldap.return.attributes");
            if (returnAttrStr != null) {
                String[] attrs = returnAttrStr.split(",");
                luas.setReturnAttributes(attrs);
            }
            uas = luas;
        } else {
            // use dummyuserattributesservice to remove dependency to LDAP
            // if not configured
            uas = new DummyUserAttributesService();
        }

        UserServiceImpl service = new UserServiceImpl(uas,
                accessPolicyManager(), datasetService(), env.getProperty(
                        "datasets.jcr.path", String.class, "/meta"));
        return service;
    }

    @Bean
    public RepositoryService repositoryService() {
        ModeShapeRepositoryService service = new ModeShapeRepositoryService();
        service.setRepository(dataSourceConfig.repository());
        service.setCredentialsProvider(systemCredentialsProvider());
        service.setMetaPath(env.getProperty("datasets.jcr.path"));
        return service;
    }

    @Bean
    public MailService mailService() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost(env.getProperty("mail.host", "localhost"));

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", "smtp");
        mailSender.setJavaMailProperties(javaMailProperties);

        return new SpringMailServiceImpl(mailSender,
                env.getProperty("mail.from"));
    }

    @Bean(name = "distributionFormats")
    public Map<String, String> distributionFormats() {
        String formatsList = env.getProperty("datasets.formats");
        String[] formats = formatsList.split(",");

        Map<String, String> formatsMap = new HashMap<String, String>();
        for (String format : formats) {
            formatsMap.put(format, format);
        }
        return formatsMap;
    }

}
