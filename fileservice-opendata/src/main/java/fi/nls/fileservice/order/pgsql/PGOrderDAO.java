package fi.nls.fileservice.order.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.order.Customer;
import fi.nls.fileservice.order.MtpCustomer;
import fi.nls.fileservice.order.OpenDataOrder;
import fi.nls.fileservice.order.OrderDAO;
import fi.nls.fileservice.security.ExpiredOrNonexistingTokenException;
import fi.nls.fileservice.security.pgsql.AccessTokenType;

public class PGOrderDAO implements OrderDAO {

    private static Logger logger = LoggerFactory.getLogger(PGOrderDAO.class);

    private DataSource dataSource;
    private int orderValidDays;

    protected static final String QUERY_ORDER_WITH_TOKEN = "SELECT o.id,c.first_name,c.last_name,c.organisation,c.email,a.token FROM open_data_order o INNER JOIN customer c"
            + " ON o.customer_id = c.id JOIN access_token a ON o.access_token_id = a.id WHERE a.token_type = "
            + AccessTokenType.TOKEN_TYPE + " AND a.token = ?";

    protected static final String QUERY_ORDER_WITH_EMAIL = "SELECT o.id,c.first_name,c.last_name,c.organisation,c.email,a.token FROM open_data_order o INNER JOIN customer c"
            + " ON o.customer_id = c.id JOIN access_token a ON o.access_token_id = a.id WHERE a.token_type = "
            + AccessTokenType.APIKEY_TYPE + " AND c.email = ?";

    protected static final String INSERT_CUSTOMER = "INSERT INTO customer(first_name, last_name, organisation, email, language) VALUES (?,?,?,?,?)";

    public PGOrderDAO() {

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setOrderValidDays(int days) {
        this.orderValidDays = days;
    }

    protected void getCustomerFromResultSet(Customer customer, ResultSet rs)
            throws SQLException {
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setOrganisation(rs.getString("organisation"));
        customer.setEmail(rs.getString("email"));
    }

    @Override
    public void saveMtpOrder(MtpCustomer customer) throws DataAccessException {

        // we expect that before calling this getMtpUser has already been called
        // verifying, that apikey doesn't already exist with the given email

        Connection con = null;
        PreparedStatement insertApiKey = null;
        PreparedStatement insertCustomer = null;
        PreparedStatement insertOrder = null;
        ResultSet rs = null;
        try {

            con = dataSource.getConnection();
            con.setAutoCommit(false);

            String insertTokenSql = "INSERT INTO access_token(token,token_type) VALUES (?,?)";
            insertApiKey = con.prepareStatement(insertTokenSql, Statement.RETURN_GENERATED_KEYS);
            insertApiKey.setString(1, customer.getApiKey());
            insertApiKey.setInt(2, AccessTokenType.APIKEY_TYPE);
            insertApiKey.executeUpdate();
            rs = insertApiKey.getGeneratedKeys();

            if (rs.next()) {
                long tokenId = rs.getLong(1);
                rs.close();

                insertCustomer = con.prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
                insertCustomer.setString(1, customer.getFirstName());
                insertCustomer.setString(2, customer.getLastName());
                insertCustomer.setString(3, customer.getOrganisation());
                insertCustomer.setString(4, customer.getEmail());
                insertCustomer.setString(5, customer.getLanguage());
                insertCustomer.executeUpdate();

                rs = insertCustomer.getGeneratedKeys();
                if (rs.next()) {
                    long customerId = rs.getLong(1);
                    rs.close();

                    insertOrder = con.prepareStatement(
                                    "INSERT INTO open_data_order(customer_id, access_token_id) VALUES (?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                    insertOrder.setLong(1, customerId);
                    insertOrder.setLong(2, tokenId);
                    insertOrder.executeUpdate();

                    con.commit();
                }
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    logger.warn("Transaction rollback failed", e1);
                }
            }
            throw new DataAccessException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc resultset", e);
                }
            }
            if (insertApiKey != null) {
                try {
                    insertApiKey.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc statement", e);
                }
            }
            if (insertCustomer != null) {
                try {
                    insertCustomer.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc statement", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc connection", e);
                }
            }
        }
    }

    @Override
    public MtpCustomer getMtpCustomer(String email) throws DataAccessException {

        Connection con = null;
        PreparedStatement queryCustomer = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();

            queryCustomer = con.prepareStatement(QUERY_ORDER_WITH_EMAIL);
            queryCustomer.setString(1, email);

            MtpCustomer customer = null;
            rs = queryCustomer.executeQuery();
            if (rs.next()) {
                customer = new MtpCustomer();
                getCustomerFromResultSet(customer, rs);
                customer.setApiKey(rs.getString("token"));
            }
            return customer;

        } catch (SQLException sqle) {
            throw new DataAccessException(sqle);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc resultset", e);
                }
            }
            if (queryCustomer != null) {
                try {
                    queryCustomer.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc statement", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warn("Error closing jdbc connection", e);
                }
            }
        }
    }

    @Override
    public void saveOpenDataOrder(String token, OpenDataOrder order) {

        Connection con = null;
        PreparedStatement insertToken = null;
        PreparedStatement insertCustomer = null;
        PreparedStatement insertOrder = null;
        PreparedStatement insertFiles = null;
        ResultSet rs = null;

        Customer customer = order.getCustomer();

        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);

            String insertTokenSql = "INSERT INTO access_token(token,token_type,expires) VALUES (?, ?, current_timestamp + INTERVAL '"
                    + this.orderValidDays + " days')";
            insertToken = con.prepareStatement(insertTokenSql, Statement.RETURN_GENERATED_KEYS);
            insertToken.setString(1, token);
            insertToken.setInt(2, AccessTokenType.TOKEN_TYPE);
            insertToken.executeUpdate();
            rs = insertToken.getGeneratedKeys();

            if (rs.next()) {
                long tokenId = rs.getLong(1);
                rs.close();

                // For now we always store a new customer record for opendata
                // orders even if one exists
                // with this email address
                // don't really know what to do with
                // firstname,lastname,organisation which can differ
                // with each order..

                insertCustomer = con.prepareStatement(INSERT_CUSTOMER,
                        Statement.RETURN_GENERATED_KEYS);
                insertCustomer.setString(1, customer.getFirstName());
                insertCustomer.setString(2, customer.getLastName());
                insertCustomer.setString(3, customer.getOrganisation());
                insertCustomer.setString(4, customer.getEmail());
                insertCustomer.setString(5, customer.getLanguage());
                insertCustomer.executeUpdate();

                rs = insertCustomer.getGeneratedKeys();
                if (rs.next()) {
                    long customerId = rs.getLong(1);
                    rs.close();

                    insertOrder = con.prepareStatement(
                                    "INSERT INTO open_data_order(customer_id, access_token_id) VALUES (?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                    insertOrder.setLong(1, customerId);
                    insertOrder.setLong(2, tokenId);
                    insertOrder.executeUpdate();

                    rs = insertOrder.getGeneratedKeys();
                    if (rs.next()) {
                        long openDataOrderId = rs.getLong(1);

                        insertFiles = con.prepareStatement("INSERT INTO open_data_order_files(jcr_path, open_data_order_id) VALUES (?,?)");

                        List<String> files = order.getFiles();
                        if (files != null) {
                            // insert list of files as a single batch
                            for (String file : files) {
                                insertFiles.setString(1, file);
                                insertFiles.setLong(2, openDataOrderId);
                                insertFiles.addBatch();
                            }
                            insertFiles.executeBatch();
                        }
                        con.commit();
                    }
                }
            }

        } catch (SQLException sqle) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    logger.error("Error during transaction rollback", e);
                }
            }
            throw new DataAccessException("Error storing open data order", sqle);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (insertToken != null) {
                try {
                    insertToken.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (insertCustomer != null) {
                try {
                    insertCustomer.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (insertOrder != null) {
                try {
                    insertOrder.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (insertFiles != null) {
                try {
                    insertFiles.close();
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
    public OpenDataOrder getOpenDataOrder(String token) {

        Connection con = null;
        PreparedStatement queryCustomer = null;
        PreparedStatement queryFiles = null;
        ResultSet rs = null;
        ResultSet files = null;
        try {

            con = dataSource.getConnection();
            queryCustomer = con.prepareStatement(QUERY_ORDER_WITH_TOKEN);
            queryCustomer.setString(1, token);
            rs = queryCustomer.executeQuery();

            if (rs.next()) {
                OpenDataOrder order = new OpenDataOrder();

                Customer customer = new Customer();
                getCustomerFromResultSet(customer, rs);
                order.setCustomer(customer);

                queryFiles = con.prepareStatement("SELECT jcr_path FROM open_data_order_files WHERE open_data_order_id = ?");
                queryFiles.setLong(1, rs.getLong("id"));
                files = queryFiles.executeQuery();
                while (files.next()) {
                    order.getFiles().add(files.getString("jcr_path"));
                }
                return order;
            } else {
                throw new ExpiredOrNonexistingTokenException(token);
            }

        } catch (SQLException sqle) {
            throw new DataAccessException("Error getting file list for token: "
                    + token, sqle);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (files != null) {
                try {
                    files.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (queryCustomer != null) {
                try {
                    queryCustomer.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (queryFiles != null) {
                try {
                    queryFiles.close();
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

}