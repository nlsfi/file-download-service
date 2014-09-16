package fi.nls.fileservice.security.service.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.security.service.UserAttributesProvider;

public class LDAPUserAttributesProvider implements UserAttributesProvider {

    private String providerUrl;
    private String principal;
    private String credentials;
    private String searchBase;
    private String[] returnAttributes;
    private String searchFilter;
    private String securityProtocol;

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public void setSecurityProtocol(String protocol) {
        this.securityProtocol = protocol;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    /**
     * In the form (uid=%s)
     * 
     * @param filter
     */
    public void setSearchFilter(String filter) {
        this.searchFilter = filter;
    }

    public void setReturnAttributes(String[] returnAttributes) {
        this.returnAttributes = returnAttributes;
    }

    @Override
    public Map<String, String> getUserDetails(String username) {

        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl);

        if (principal != null) {
            env.put(Context.SECURITY_PRINCIPAL, principal);
        }
        if (credentials != null) {
            env.put(Context.SECURITY_CREDENTIALS, credentials);
        }

        if (securityProtocol != null) {
            env.put(Context.SECURITY_PROTOCOL, securityProtocol);
        }

        try {

            String filter = String.format(searchFilter, username);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setTimeLimit(300);
            constraints.setDerefLinkFlag(false);
            constraints.setReturningObjFlag(false);
            constraints.setCountLimit(5);
            constraints.setReturningAttributes(returnAttributes);

            InitialDirContext ctx = new InitialDirContext(env);

            NamingEnumeration<SearchResult> results = ctx.search(searchBase,
                    filter, constraints);

            Map<String, String> attributes = new HashMap<String, String>();

            while (results.hasMore()) {
                SearchResult result = results.next();

                Attributes attrs1 = result.getAttributes();
                NamingEnumeration<String> ids = attrs1.getIDs();

                while (ids.hasMore()) {
                    String id_attr = ids.next().toString();
                    NamingEnumeration<?> values = result.getAttributes()
                            .get(id_attr).getAll();
                    while (values.hasMore()) {
                        String value = values.next().toString();
                        attributes.put(id_attr, value);
                    }
                }
            }
            return attributes;

        } catch (NamingException e) {
            throw new DataAccessException(e);
        }
    }

}
