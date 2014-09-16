package fi.nls.fileservice.security.pgsql;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Abstract base class for classes using DataSource
 * 
 */
public abstract class DBBase {

    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSourceName(String name) {
        try {
            InitialContext ctx = new InitialContext();
            this.dataSource = (DataSource) ctx.lookup(name);
        } catch (NamingException e) {
            throw new RuntimeException("Error acquiring datasource with name "
                    + name, e);
        }
    }

}
