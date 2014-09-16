package fi.nls.fileservice.security.jcr;

import javax.jcr.Credentials;

/**
 * Utility interface to provide JCR repository credentials to internal system
 * functions.
 * 
 */
public interface CredentialsProvider {

    /**
     * Return credentials for JCR repository
     * 
     * @return credentials
     */
    public Credentials getCredentials();
}
