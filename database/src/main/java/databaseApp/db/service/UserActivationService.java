package databaseApp.db.service;

import databaseApp.db.model.event.UserRegisteredEvent;

public interface UserActivationService {

    void userRegistered(UserRegisteredEvent event);
}
