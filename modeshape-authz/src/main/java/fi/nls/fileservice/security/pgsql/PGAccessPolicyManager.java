package fi.nls.fileservice.security.pgsql;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.ACE;
import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.AccessPolicyImpl;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.ChangeSet;
import fi.nls.fileservice.security.PolicyAccessException;
import fi.nls.fileservice.security.Privilege;

public class PGAccessPolicyManager extends DBBase implements AccessPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(PGAccessPolicyManager.class);

    public PGAccessPolicyManager() {
        super();
    }

    @Override
    public AccessPolicy getAccessPolicy(String username) {

        Connection con = null;
        PreparedStatement queryPrivileges = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();

            StringBuilder query = new StringBuilder("SELECT a.username, p.id, p.jcr_path, p.privileges ");
            query.append("FROM account a LEFT JOIN jcr_permission p ON a.id = p.account_id ");
            query.append("WHERE a.username = ?");
            
            queryPrivileges = con.prepareStatement(query.toString());
            queryPrivileges.setString(1, username);
            rs = queryPrivileges.executeQuery();

            if (rs.next()) {

                List<ACE> acis = new ArrayList<ACE>();

                do {
                    long id = rs.getLong("id");
                    String path = rs.getString("jcr_path");
                    Array array = rs.getArray("privileges");

                    if (path != null && array != null) {
                        Set<Privilege> privileges = privilegesFromJdbcArray(array);

                        ACE aci = new ACE(id, path);
                        aci.setPrivileges(privileges);
                        acis.add(aci);

                        if (logger.isDebugEnabled()) {
                            PGAccessPolicyManager.logger.debug(
                                    "Privileges of user {} to path {}: ",
                                    username, path, Arrays.toString((Object[])array.getArray()));
                        }
                    }

                } while (rs.next());

                return new AccessPolicyImpl(username, acis);
            }

            // policy not found
            logger.warn("Access policy not found for principal: '{}'", username);
            return null;

        } catch (SQLException sqle) {
            throw new PolicyAccessException(
                    "Error getting permissions for user: " + username, sqle);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (queryPrivileges != null) {
                try {
                    queryPrivileges.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
        }
    }

    @Override
    public void saveAccessPolicy(AccessPolicy policy) {

        AccessPolicyImpl aPolicy = (AccessPolicyImpl) policy;

        // don't save policy that has not been modified or is not new
        if (!aPolicy.isModified()) {
            logger.info("Access policy for {} not modified, skipping saving");
            return;
        }

        Connection con = null;
        PreparedStatement queryAccount = null;
        PreparedStatement updatePrivileges = null;
        ResultSet rsQuery = null;

        try {

            long accountId = -1;

            con = dataSource.getConnection();
            con.setAutoCommit(false);

            // check if account exists already
            queryAccount = con
                    .prepareStatement("SELECT id, username FROM account WHERE account.username = ?");
            queryAccount.setString(1, aPolicy.getUid());

            rsQuery = queryAccount.executeQuery();
            if (rsQuery.next()) {
                accountId = rsQuery.getLong("id");
            } else {
                rsQuery.close();
                queryAccount.close();
                // new user
                queryAccount = con.prepareStatement("INSERT INTO account(username) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS);
                queryAccount.setString(1, aPolicy.getUid());
                queryAccount.executeUpdate();

                rsQuery = queryAccount.getGeneratedKeys();

                if (rsQuery.next()) {
                    accountId = rsQuery.getLong(1);
                }
            }

            if (aPolicy.isModified()) {
                
                updatePrivileges = con.prepareStatement("DELETE FROM jcr_permission WHERE id = ?");
                applyChanges(aPolicy.getChanges(), new AbstractChangeSetPredicate(updatePrivileges) {

                    @Override
                    public void apply(ChangeSet set) throws SQLException {
                        if (ChangeSet.ChangeType.REMOVE == set.getType()) {
                            setUpdates();
                            smnt.setLong(1, set.getACE().getId());
                            smnt.addBatch();
                        }
                    }
                    
                });
                
                updatePrivileges = con.prepareStatement("UPDATE jcr_permission SET jcr_path = ?, privileges = ? WHERE id = ?");
                applyChanges(aPolicy.getChanges(), new AbstractChangeSetPredicate(updatePrivileges) {

                    @Override
                    public void apply(ChangeSet set) throws SQLException {
                        if (ChangeSet.ChangeType.MODIFY == set.getType()) {
                            setUpdates();
                            smnt.setString(1, set.getACE().getPath());
                            Array arr = PGAccessPolicyManager.privilegesToJdbcArray(smnt.getConnection(),
                                    set.getACE().getPrivileges().toArray());
                            smnt.setArray(2, arr);
                            smnt.setLong(3, set.getACE().getId());
                            smnt.addBatch();
                        }
                    }
                        
                });
                    
                updatePrivileges = con.prepareStatement("INSERT INTO jcr_permission(account_id,jcr_path,privileges) VALUES (?,?,?)");
                applyChanges(aPolicy.getChanges(), new AbstractChangeSetPredicate(updatePrivileges, accountId) {

                    @Override
                    public void apply(ChangeSet set) throws SQLException {
                        if (ChangeSet.ChangeType.ADD == set.getType()) {
                            setUpdates();
                            Array arr = PGAccessPolicyManager.privilegesToJdbcArray(
                                    smnt.getConnection(), set.getACE().getPrivileges().toArray());
                            smnt.setLong(1, accountId);
                            smnt.setString(2, set.getACE().getPath());
                            smnt.setArray(3, arr);
                            smnt.addBatch();
                        }
                    }
                        
                });
                
                con.commit();
                    
                logger.info("Updated access policy for principal: {}", aPolicy.getUid());
            }

        } catch (SQLException sqle) {
            throw new PolicyAccessException("Error saving access policy", sqle);
        } finally {
            if (rsQuery != null) {
                try {
                    rsQuery.close();
                } catch (SQLException ex1) {
                    logger.warn("Error closing", ex1);
                }
            }
            if (updatePrivileges != null) {
                try {
                    updatePrivileges.close();
                } catch (SQLException ex2) {
                    logger.warn("Error closing", ex2);
                }
            }
            if (updatePrivileges != null) {
                try {
                    updatePrivileges.close();
                } catch (SQLException ex2) {
                    logger.warn("Error closing", ex2);
                }
            }
            if (queryAccount != null) {
                try {
                    queryAccount.close();
                } catch (SQLException ex2) {
                    logger.warn("Error closing", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex3) {
                    logger.warn("Error closing", ex3);
                }
            }
        }
    }

    public static void applyChanges(Collection<ChangeSet> changes, ChangeSetPredicate<ChangeSet> predicate)
            throws SQLException {
        for (ChangeSet set : changes) {
            predicate.apply(set);
        }
        predicate.save();
    }
    
    protected static Array privilegesToJdbcArray(Connection con, Object[] privileges) throws SQLException {
        Object[] privilegeNames = new Object[privileges.length];
        for (int i = 0; i < privileges.length; i++) {
            privilegeNames[i] = ((Privilege) privileges[i]).getName();
        }
        // must use lowercase "varchar" here because of PG JDBC driver
        // implementation
        // this also requires JDBC4 compatible driver, at least Commons
        // DBCP 1.4 returns null here
        return con.createArrayOf("varchar", privilegeNames);
    }
    
    protected static Set<Privilege> privilegesFromJdbcArray(Array array) throws SQLException {
        // HSQLDB jdbc driver doesn't allow casting to String[]
        // here..
        Object[] privilegesArr = (Object[]) array.getArray();
        Set<Privilege> privileges = new HashSet<Privilege>(privilegesArr.length);
        for (Object p : privilegesArr) {
            privileges.add(Privilege.forName(p.toString()));
        }
        return privileges;
    }

}
