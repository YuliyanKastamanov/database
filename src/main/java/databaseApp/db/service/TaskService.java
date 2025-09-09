package databaseApp.db.service;

import databaseApp.db.model.dto.*;
import databaseApp.db.model.entity.enums.TaskTypeEnum;

import java.util.List;

public interface TaskService {
    void addTask(AddTaskDTO addTaskDTO, TaskTypeEnum type, String revision);

    boolean existByCri(String taskNumber, TaskTypeEnum taskTypeEnum);

    List<ReturnTaskDTO> getAllTasksByTaskType(TaskTypeEnum type);


    void taskRevision(AddTaskDTO currentTask, TaskTypeEnum type, String revision);

    List<ReturnTaskDTO> getAll();

    void addNewRevision(AddTaskDTOs addTaskDTOs);

    List<AddTaskResponseDTO> addAllTasks(AddTaskDTOs addTaskDTOs);

    List<ReturnTaskDTO> getAllTasksWithoutOk(CheckTaskStatusDTO checkTaskStatusDTO);

    List<ReturnTaskDTO> updateAllTasks(List<UpdateTaskDTO> updateTaskDTOS);
}
