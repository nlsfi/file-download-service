package fi.nls.fileservice.mail;

import java.util.Map;

public interface TemplateResolver {

    String getMessage(String template, Map<String, Object> parameters);
}
