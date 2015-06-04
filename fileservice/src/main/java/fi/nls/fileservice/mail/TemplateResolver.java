package fi.nls.fileservice.mail;

import java.util.Locale;
import java.util.Map;

public interface TemplateResolver {

    String getMessage(String template, Locale locale, Map<String, Object> parameters);
}
