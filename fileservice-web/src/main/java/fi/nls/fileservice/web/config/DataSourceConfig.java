package fi.nls.fileservice.web.config;

import java.util.Iterator;

import javax.jcr.Repository;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.modeshape.common.collection.Problem;
import org.modeshape.common.collection.Problems;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;

@Configuration
public class DataSourceConfig {

    @Bean(destroyMethod = "shutdown")
    public ModeShapeEngine modeshapeEngine() {
        ModeShapeEngine engine = new ModeShapeEngine();
        engine.start();
        return engine;
    }

    @Bean
    public Repository repository() {

        ModeShapeEngine engine = modeshapeEngine();

        try {
            RepositoryConfiguration config = RepositoryConfiguration.read(
                    Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("repository.json"),"file_service");

            Problems problems = config.validate();
            if (problems.hasErrors()) {
                Iterator<Problem> probIter = problems.iterator();
                while (probIter.hasNext()) {
                    System.err.println(probIter.next().getMessage());
                }
                throw new RuntimeException("Problems with JCR configuration.");
            }

            javax.jcr.Repository repo = engine.deploy(config);
            return repo;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load JCR repository", e);
        }
    }

    @Bean
    public DataSource orderDataSource() {
        JndiTemplate template = new JndiTemplate();
        try {
            return template.lookup("java:comp/env/jdbc/tiepaldb",
                    DataSource.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
