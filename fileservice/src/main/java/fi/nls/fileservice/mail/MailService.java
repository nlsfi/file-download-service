package fi.nls.fileservice.mail;

public interface MailService {

    public void sendMessage(String to, String Subject, String body);
}
