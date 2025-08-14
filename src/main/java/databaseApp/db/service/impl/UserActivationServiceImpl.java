/*
package databaseApp.db.service.impl;

import databaseApp.db.event.UserRegisteredEvent;
import databaseApp.db.service.EmailService;
import databaseApp.db.service.UserActivationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final EmailService emailService;

    public UserActivationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @EventListener(UserRegisteredEvent.class)
    public void userRegistered(UserRegisteredEvent event) {
        emailService.sendRegistrationEmail(event.getUserEmail(), event.getUserNames());

    }
}
*/
