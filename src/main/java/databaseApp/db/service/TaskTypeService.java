package databaseApp.db.service;

import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.enums.TaskTypeEnum;

public interface TaskTypeService {

    void initDbTypes();


    TaskTypeEntity findByType(TaskTypeEnum type);

    void updateRevision(TaskTypeEnum type, String dbRevision);
}
