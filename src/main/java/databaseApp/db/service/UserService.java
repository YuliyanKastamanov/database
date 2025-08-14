package databaseApp.db.service;

import databaseApp.db.model.dto.PasswordDto;
import databaseApp.db.model.entity.UserEntity;

public interface UserService {


    UserEntity findByUNumber(String uNumber);

    void changePassword(PasswordDto passwordDto);

    void initUser();
}
