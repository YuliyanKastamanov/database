package databaseApp.db.service;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.model.entity.enums.TaskTypeEnum;

public interface TaskService {
    void addTask(AddTaskDTO addTaskDTO);


    boolean existByCri(String taskNumber, TaskTypeEnum taskTypeEnum);
}
