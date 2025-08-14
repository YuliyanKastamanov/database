package databaseApp.db.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


public class MailConfiguration {

    @Bean
    public JavaMailSender javaMailSender(
            @Value("localhost") String host,
            @Value("2025") int port,
            @Value("softuni") String username,
            @Value("softuni}") String password) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setJavaMailProperties(mailProperties());

        return javaMailSender;
    }

    private Properties mailProperties() {
        Properties properties = new Properties();

        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.transport.protocol", "smtp");

        return properties;
    }
}