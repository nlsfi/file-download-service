package fi.nls.fileservice.security.pgsql;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.ACE;
import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.AccessPolicyImpl;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.PolicyAccessException;
import fi.nls.fileservice.security.Privilege;

public class PGAccessPolicyManager extends DBBase implements
        AccessPolicyManager {

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

            queryPrivileges = con
                    .prepareStatement("SELECT a.username, p.jcr_path, p.privileges"
                            + " FROM account a LEFT JOIN jcr_permission p ON a.id = p.account_id"
                            + " WHERE a.username = ?");
            queryPrivileges.setString(1, username);
            rs = queryPrivileges.executeQuery();

            if (rs.next()) {

                List<ACE> acis = new ArrayList<ACE>();

                do {
                    String path = rs.getString("jcr_path");
                    Array array = rs.getArray("privileges");
                  
                    if (path != null && array != null) {
                         //HSQLDB jdbc driver doesn't allow casting to String[] here..
                        Object[] privilegesArr =  (Object[]) array.getArray();
                        List<Privilege> privileges = new ArrayList<Privilege>(privilegesArr.length);
                        for (Object p : privilegesArr) {
                            privileges.add(Privilege.forName(p.toString()));
                        }
                        
                        ACE aci = new ACE();
                        aci.setPath(path);
                        aci.setPrivileges(privileges);
                        acis.add(aci);

                        if (logger.isDebugEnabled()) {
                            PGAccessPolicyManager.logger
                                    .debug("Privileges from DB: " + username
                                            + ":" + path + "->"
                                            + Arrays.toString(privilegesArr));
                        }
                    }

                } while (rs.next());

                return new AccessPolicyImpl(username, acis);
            }

            // policy not found
            logger.warn("Access policy not found for principal: '" + username
                    + "'");
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
            return;
        }

        Connection con = null;
        PreparedStatement queryAccount = null;
        PreparedStatement deleteExistingPrivileges = null;
        PreparedStatement updatePrivileges = null;
        ResultSet rsQuery = null;

        try {

            long id = -1;

            con = dataSource.getConnection();
            con.setAutoCommit(false);

            // check if user exists already
            queryAccount = con
                    .prepareStatement("SELECT id, username FROM account WHERE account.username = ?");
            queryAccount.setString(1, aPolicy.getUid());

            rsQuery = queryAccount.executeQuery();

            if (rsQuery.next()) {
                id = rsQuery.getLong("id");
                // delete possible existing permissions
                deleteExistingPrivileges = con
                        .prepareStatement("DELETE FROM jcr_permission WHERE account_id = ?");
                deleteExistingPrivileges.setLong(1, id);
                deleteExistingPrivileges.executeUpdate();
            } else {
                rsQuery.close();
                queryAccount.close();
                // new user
                queryAccount = con.prepareStatement(
                        "INSERT INTO account(username) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS);
                queryAccount.setString(1, aPolicy.getUid());
                queryAccount.executeUpdate();

                rsQuery = queryAccount.getGeneratedKeys();

                if (rsQuery.next()) {
                    id = rsQuery.getLong(1);
                }
            }

            List<ACE> acis = aPolicy.getAcis();

            updatePrivileges = con
                    .prepareStatement("INSERT INTO jcr_permission(account_id,jcr_path,privileges) VALUES (?,?,?)");

            for (ACE aci : acis) {
                // convert Privilege objects to string array for JDBC
                Object[] privileges = aci.getPrivileges().toArray();
                Object[] privilegeNames = new Object[privileges.length];
                for (int i = 0; i < privileges.length; i++) {
                    privilegeNames[i] = ((Privilege) privileges[i]).getName();
                }
                // must use lowercase "varchar" here because of PG JDBC driver
                // implementation
                // this also requires JDBC4 compatible driver, at least Commons
                // DBCP 1.4 returns null here
                Array arr = con.createArrayOf("varchar", privilegeNames);
                updatePrivileges.setLong(1, id);
                updatePrivileges.setString(2, aci.getPath());
                updatePrivileges.setArray(3, arr);
                updatePrivileges.addBatch();
            }

            updatePrivileges.executeBatch();
            con.commit();

            logger.info("Updated access policy for principal: "
                    + aPolicy.getUid());

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
            if (deleteExistingPrivileges != null) {
                try {
                    deleteExistingPrivileges.close();
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

}
