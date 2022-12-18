package sit.project221.oasipbackend.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail (String toMail , String subject , String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("oasip.pl2.test@gmail.com");
        msg.setTo(toMail);
        msg.setText(body);
        msg.setSubject(subject);

        mailSender.send(msg);
        System.out.println("Email Sent successfully");
    }
}
