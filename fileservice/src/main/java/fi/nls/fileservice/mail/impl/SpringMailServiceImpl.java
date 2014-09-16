package fi.nls.fileservice.mail.impl;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import fi.nls.fileservice.mail.MailService;

public class SpringMailServiceImpl implements MailService {

    private final MailSender mailSender;
    private final String from;

    public SpringMailServiceImpl(MailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
