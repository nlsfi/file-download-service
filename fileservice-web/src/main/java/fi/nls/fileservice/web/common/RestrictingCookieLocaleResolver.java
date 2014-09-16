package fi.nls.fileservice.web.common;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Spring MVC Locale Resolver that adds support for restricting allowed locales.
 */
public class RestrictingCookieLocaleResolver extends CookieLocaleResolver {

    protected String[] supportedLanguages;

    public RestrictingCookieLocaleResolver() {
        super();
    }

    public void setSupportedLanguages(String[] languages) {
        this.supportedLanguages = languages;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // Check request for pre-parsed or preset locale.
        Locale locale = (Locale) request
                .getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale == null) {

            // Retrieve and parse cookie value.
            Cookie cookie = WebUtils.getCookie(request, getCookieName());
            if (cookie != null) {
                locale = StringUtils.parseLocaleString(cookie.getValue());
                if (logger.isDebugEnabled()) {
                    logger.debug("Parsed cookie value [" + cookie.getValue()
                            + "] into locale '" + locale + "'");
                }
            }
        }

        if (locale != null) {

            if (supportedLanguages != null) {
                for (String lang : supportedLanguages) {
                    if (lang.equals(locale.getLanguage())) {
                        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
                                locale);
                        return locale;
                    }
                }
            } else {
                request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
                return locale;
            }
        }

        return determineDefaultLocale(request);
    }

}
