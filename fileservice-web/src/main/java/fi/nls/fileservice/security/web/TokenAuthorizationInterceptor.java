package fi.nls.fileservice.security.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Credentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.jcr.TemporaryTokenCredentials;

public class TokenAuthorizationInterceptor extends BaseAuthorizationInterceptor {

    private Pattern regex = Pattern
            .compile("\\S*\\/tilaus\\/(\\S*?)(\\/\\S*)*");
    protected String mappedAccount;

    public void setMappedAccount(String account) {
        this.mappedAccount = account;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter("token");
        if (token == null || "".equals(token)) {
            String path = request.getRequestURI();
            Matcher matcher = regex.matcher(path);
            if (matcher.matches()) {
                token = matcher.group(1);
            }
        }

        if (token != null) {
            Credentials credentials = new TemporaryTokenCredentials(token,
                    mappedAccount);
            AuthorizationContextHolder.setCredentials(credentials);
            request.setAttribute("fi.nls.lapa.serviceid", "map");
            return true;

        }

        // allow processing to continue even if there are no JCR Credentials
        // (eg. POST /tilaus)
        return true;
    }

}
