package databaseApp.db.service;

import databaseApp.db.event.UserRegisteredEvent;

public interface UserActivationService {

    void userRegistered(UserRegisteredEvent event);
}
