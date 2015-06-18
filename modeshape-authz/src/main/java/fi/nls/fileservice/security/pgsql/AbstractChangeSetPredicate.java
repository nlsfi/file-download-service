package fi.nls.fileservice.security.pgsql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import fi.nls.fileservice.security.ChangeSet;

public abstract class AbstractChangeSetPredicate implements ChangeSetPredicate<ChangeSet> {

    protected final PreparedStatement smnt;
    protected boolean hasUpdates = false;
    protected final long accountId;
    
    public AbstractChangeSetPredicate(PreparedStatement smnt) {
        this(smnt, -1);
    }
    
    public AbstractChangeSetPredicate(PreparedStatement smnt, long accountId) {
        this.smnt = smnt;
        this.accountId = accountId;
    }
    
    protected void setUpdates() {
        this.hasUpdates = true;
    }
    
    @Override
    public abstract void apply(ChangeSet set) throws SQLException;
    
    @Override
    public void save() throws SQLException {
        if (hasUpdates) {
            smnt.executeBatch();
            smnt.close();
        }
    }
    
}
