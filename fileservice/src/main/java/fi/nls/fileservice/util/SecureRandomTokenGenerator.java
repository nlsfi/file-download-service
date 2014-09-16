package fi.nls.fileservice.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureRandomTokenGenerator implements TokenGenerator {

    private SecureRandom random;

    /**
     * Constructs a secure random token generator
     */
    public SecureRandomTokenGenerator() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            // this is a fatal environment error, so we fail immediately
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a new random token
     * 
     * @return token
     */
    @Override
    public String generateToken() {
        // generate random number in range [0, 2^130-1[
        // and encode as base32
        return new BigInteger(130, random).toString(32);
    }

}
