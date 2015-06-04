package fi.nls.fileservice.web.config;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jcr.Credentials;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import fi.nls.fileservice.jcr.repository.MetadataUpdateService;
import fi.nls.fileservice.jcr.repository.MetadataUpdateServiceImpl;
import fi.nls.fileservice.jcr.repository.ScriptProvider;
import fi.nls.fileservice.security.jcr.CredentialsProvider;
import fi.nls.fileservice.security.jcr.ExternalAuthenticationCredentials;
import fi.nls.fileservice.security.web.ApiKeyAuthorizationInterceptor;
import fi.nls.fileservice.security.web.AuthorizationInterceptor;
import fi.nls.fileservice.security.web.ImpersonatingAuthorizationInterceptor;
import fi.nls.fileservice.security.web.TokenAuthorizationInterceptor;
import fi.nls.fileservice.system.StatusChecker;
import fi.nls.fileservice.web.common.RestrictingCookieLocaleResolver;
import fi.nls.fileservice.web.common.ServletContextScriptProvider;
import fi.nls.fileservice.web.common.SpringMessageSourceMessageInterpolator;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "fi.nls.fileservice.web")
@Import({ ApplicationConfig.class })
public class WebConfig extends WebMvcConfigurerAdapter implements
        ServletContextAware {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    private ServletContext servletContext;

    @Autowired
    private Environment env;

    @Override
    public void setServletContext(ServletContext context) {
        this.servletContext = context;
    }

    @Bean
    public MetadataUpdateService metadataUpdateService() {
        ExecutorService executor = Executors.newCachedThreadPool();

        ScriptProvider scriptProvider = new ServletContextScriptProvider(
                servletContext, env.getProperty("metadata.scripts.path"));

        MetadataUpdateServiceImpl service = new MetadataUpdateServiceImpl(
                executor, scriptProvider, dataSourceConfig.repository(),
                applicationConfig.systemCredentialsProvider(),
                applicationConfig.datasetDAO(),
                env.getProperty("metadataupdate.task.save_changes_in_batches_of", Integer.class, 100));
        return service;
    }

    @Bean
    public InternalResourceViewResolver configureInternalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setRequestContextAttribute("rc");
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
/*
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        SpringMessageSourceMessageInterpolator interpolator = new SpringMessageSourceMessageInterpolator(
                applicationConfig.messageSource());
        validator.setMessageInterpolator(interpolator);
        return validator;
    }*/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/").setCachePeriod(86400);
    }

    @Bean
    public LocaleResolver localeResolver() {
        RestrictingCookieLocaleResolver resolver = new RestrictingCookieLocaleResolver();

        String languageStr = env.getProperty("feed.languages");
        if (languageStr != null) {
            String[] languages = languageStr.split(",");
            resolver.setSupportedLanguages(languages);
        }

        resolver.setCookieName("user.locale");
        resolver.setDefaultLocale(new Locale("fi", "FI"));
        return resolver;
    }

    @Bean
    public StatusChecker statusChecker() {
        StatusChecker checker = new StatusChecker();
        checker.setDataSource(dataSourceConfig.orderDataSource());
        checker.setRepository(dataSourceConfig.repository());
        checker.setCredentialsProvider(applicationConfig
                .systemCredentialsProvider());
        return checker;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**");

        // Authenticated external users
        AuthorizationInterceptor authInterceptor = new AuthorizationInterceptor();
        authInterceptor.setUserNameHeader("uid");
        registry.addInterceptor(authInterceptor).addPathPatterns(
                "/feed/inspire/**", "/lataus/**");

        // Internal users and APIs
        ImpersonatingAuthorizationInterceptor internalAuthInterceptor = new ImpersonatingAuthorizationInterceptor(
                applicationConfig.systemCredentialsProvider());
        registry.addInterceptor(internalAuthInterceptor).addPathPatterns(
                "/hallinta/**", "/api/**", "/service/**", "/tilastot/**");

        // Fully open services
        // We must 'authenticate' to ModeShape using internal opendata account
        // because
        // anonymous login is not allowed
        ImpersonatingAuthorizationInterceptor openDataInterceptor = new ImpersonatingAuthorizationInterceptor(
                new CredentialsProvider() {

                    @Override
                    public Credentials getCredentials() {
                        return new ExternalAuthenticationCredentials(env
                                .getProperty("opendata.account.name"), null);
                    }

                });
        registry.addInterceptor(openDataInterceptor).addPathPatterns(
                "/kartta/**");

        // Requests "authenticated" by a temporary token
        TokenAuthorizationInterceptor tokenAuthInterceptor = new TokenAuthorizationInterceptor();
        tokenAuthInterceptor.setMappedAccount(env
                .getProperty("opendata.account.name"));
        registry.addInterceptor(tokenAuthInterceptor).addPathPatterns(
                "/tilaus/**", "/tilauslataus/**");

        // Updating service users authenticated by API key
        ApiKeyAuthorizationInterceptor apiKeyAuthInterceptor = new ApiKeyAuthorizationInterceptor();
        apiKeyAuthInterceptor.setMappedAccount(env
                .getProperty("opendata.account.name"));
        registry.addInterceptor(apiKeyAuthInterceptor).addPathPatterns(
                "/feed/mtp/**", "/tilauslataus/**");

    }

}
