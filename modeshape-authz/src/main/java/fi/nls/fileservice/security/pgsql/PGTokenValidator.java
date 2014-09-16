package fi.nls.fileservice.security.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.nls.fileservice.security.PolicyAccessException;

public class PGTokenValidator extends DBBase {

    private static final Logger logger = LoggerFactory.getLogger(PGTokenValidator.class);

    private static final String API_KEY_VALIDATION_QUERY = "SELECT token FROM access_token WHERE token_type = '"
            + AccessTokenType.APIKEY_TYPE + "' AND token = ?";
    private static String TOKEN_VALIDATION_QUERY = "SELECT token FROM access_token WHERE token_type = '"
            + AccessTokenType.TOKEN_TYPE
            + "' and expires > current_timestamp AND token = ?";

    public PGTokenValidator() {
        super();
    }

    public boolean isApiKeyValid(String apiKey) {
        return validateToken(API_KEY_VALIDATION_QUERY, apiKey);
    }

    public boolean isTokenValid(String token) {
        return validateToken(TOKEN_VALIDATION_QUERY, token);
    }

    private boolean validateToken(String query, String token) {

        Connection con = null;
        PreparedStatement tokenQuery = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            tokenQuery = con.prepareStatement(query);
            tokenQuery.setString(1, token);
            resultSet = tokenQuery.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException sqle) {
            throw new PolicyAccessException("Error validating token: " + token,
                    sqle);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.warn("Error closing", e);
                }
            }
            if (tokenQuery != null) {
                try {
                    tokenQuery.close();
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
        return false;
    }

}
