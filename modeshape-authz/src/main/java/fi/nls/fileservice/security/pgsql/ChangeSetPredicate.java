package fi.nls.fileservice.security.pgsql;

import java.sql.SQLException;

public interface ChangeSetPredicate<T> {

    void apply(T type) throws SQLException;
    
    void save() throws SQLException;
}
