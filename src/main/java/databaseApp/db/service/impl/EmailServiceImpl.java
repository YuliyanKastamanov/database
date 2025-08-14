/*
package databaseApp.db.service.impl;

import databaseApp.db.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final String dbEmail;

    public EmailServiceImpl(TemplateEngine templateEngine,
                            JavaMailSender javaMailSender,
                            @Value("db-app@example.com") String dbEmail) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.dbEmail = dbEmail;
    }

    @Override
    public void sendRegistrationEmail(String userEmail, String userName) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setTo(userEmail);
            mimeMessageHelper.setFrom(dbEmail);
            mimeMessageHelper.setReplyTo(dbEmail);
            mimeMessageHelper.setSubject("Welcome to database application!");
            mimeMessageHelper.setText(generateRegistrationEmailBody(userName), true);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRegistrationEmailBody(String userName){

       String text = "Please follow the link in order to activate your account.";

       return text;
    }
}
*/
