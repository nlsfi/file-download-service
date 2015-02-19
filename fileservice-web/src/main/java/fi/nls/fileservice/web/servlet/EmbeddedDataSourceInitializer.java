package fi.nls.fileservice.web.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * ServletContextListener that creates an embedded JDBC datasource (HSQL) and
 * registers it as JNDI resource
 * 
 * This is used only in tests and during development
 */
public class EmbeddedDataSourceInitializer implements ServletContextListener {

    private EmbeddedDatabase dataSource;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (dataSource != null) {
            dataSource.shutdown();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            InitialContext ctx = new InitialContext();
            Context javaCompEnv = (Context) ctx.lookup("java:comp/env");
            Context jdbcCtx = javaCompEnv.createSubcontext("jdbc");

            // an embedded HSQLDB with PostgreSQL syntax enabled
            this.dataSource = new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.HSQL)
                    .setName("tiepaldb;sql.syntax_pgs=true")
                    .addScript("file:../resources/sql/create-tables.sql")
                    .addScript("file:../resources/sql/data-hsql.sql").build();
            jdbcCtx.rebind("tiepaldb", dataSource);
        } catch (NamingException ne) {
            throw new RuntimeException("Failed to initialize JNDI DataSource",
                    ne);
        }
    }

}
