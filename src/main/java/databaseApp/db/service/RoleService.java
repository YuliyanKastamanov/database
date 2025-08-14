package databaseApp.db.service;

import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.enums.RoleEnum;

public interface RoleService {

    void initRoles();


    RoleEntity findByName(RoleEnum role);
}
