package databaseApp.db.service;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.model.dto.AddTaskResponseDTO;
import databaseApp.db.model.dto.CheckTaskStatusDTO;
import databaseApp.db.model.dto.ReturnTaskDTO;
import databaseApp.db.model.entity.enums.TaskTypeEnum;

import java.util.List;

public interface TaskService {
    void addTask(AddTaskDTO addTaskDTO);

    boolean existByCri(String taskNumber, String taskTypeEnum);

    List<ReturnTaskDTO> getAllTasksByTaskType(TaskTypeEnum type);


    void changeTask(AddTaskDTO currentTask);

    List<ReturnTaskDTO> getAll();

    void addNewRevision(List<AddTaskDTO> addTaskDTOs);

    List<AddTaskResponseDTO> addAllTasks(List<AddTaskDTO> addTaskDTOs);

    List<ReturnTaskDTO> getAllTasksWithoutOk(CheckTaskStatusDTO checkTaskStatusDTO);
}
