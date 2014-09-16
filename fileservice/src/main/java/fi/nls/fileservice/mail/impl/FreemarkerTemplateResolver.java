package fi.nls.fileservice.mail.impl;

import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import fi.nls.fileservice.mail.TemplateResolver;
import fi.nls.fileservice.mail.TemplateResolvingException;
import freemarker.template.Configuration;

public class FreemarkerTemplateResolver implements TemplateResolver {

    private final Configuration freemarkerConfiguration;

    public FreemarkerTemplateResolver(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public String getMessage(String template, Map<String, Object> model) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate(template), model);
        } catch (Exception e) {
            throw new TemplateResolvingException("Error processing template: "
                    + template, e);
        }
    }

}
