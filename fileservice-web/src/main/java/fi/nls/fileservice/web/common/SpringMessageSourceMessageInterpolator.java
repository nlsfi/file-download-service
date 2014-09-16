package fi.nls.fileservice.web.common;

import java.util.Locale;

import javax.validation.MessageInterpolator;

import org.springframework.context.MessageSource;

public class SpringMessageSourceMessageInterpolator implements
        MessageInterpolator {

    private MessageSource messageSource;

    public SpringMessageSourceMessageInterpolator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return messageSource.getMessage(messageTemplate, new Object[] {},
                messageTemplate, Locale.getDefault());
    }

    @Override
    public String interpolate(String messageTemplate, Context arg1,
            Locale locale) {
        return messageSource.getMessage(messageTemplate, new Object[] {},
                messageTemplate, locale);
    }

}
