package fi.nls.fileservice.web.config;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.jcr.Repository;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.modeshape.common.collection.Problem;
import org.modeshape.common.collection.Problems;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.RepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiTemplate;

@Configuration
public class DataSourceConfig {
    
    private ModeShapeEngine modeshapeEngine;

    @Autowired
    private Environment env;

    @Bean
    public Repository repository() {

        this.modeshapeEngine = new ModeShapeEngine();
        this.modeshapeEngine.start();

        try {
            // FIXME: Setup lock store location for JBoss Transaction Service
            // without this ${user.home} is used to which the application server may not
            // have write permissions
            String tempDir = env.getProperty("java.io.tmpdir");
            System.setProperty("com.arjuna.ats.arjuna.objectstore.objectStoreDir", tempDir);
            System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir", tempDir);

            RepositoryConfiguration config = RepositoryConfiguration.read(
                    Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("repository.json"),"file_service");

            Problems problems = config.validate();
            if (problems.hasErrors()) {
                Iterator<Problem> probIter = problems.iterator();
                while (probIter.hasNext()) {
                    System.err.println(probIter.next().getMessage());
                }
            }
            return modeshapeEngine.deploy(config);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load JCR repository", e);
        }
    }

    @Bean
    public DataSource orderDataSource() {
        JndiTemplate template = new JndiTemplate();
        try {
            return template.lookup("java:comp/env/jdbc/tiepaldb", DataSource.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        if (this.modeshapeEngine != null) {
            Future<Boolean> future = this.modeshapeEngine.shutdown();
            try {
                future.get(); // wait for repository to properly shutdown
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
