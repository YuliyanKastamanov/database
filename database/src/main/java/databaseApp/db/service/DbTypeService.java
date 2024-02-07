package databaseApp.db.service;

import databaseApp.db.model.entity.TypeEntity;
import databaseApp.db.model.entity.enums.TypeEnum;

public interface DbTypeService {

    void initDbTypes();


    TypeEntity findByType(TypeEnum name);
}
