package databaseApp.db.service;

import databaseApp.db.model.dto.*;
import databaseApp.db.model.entity.enums.TaskTypeEnum;

import java.util.List;

public interface TaskService {
    void addTask(AddTaskDTO addTaskDTO);

    boolean existByCri(String taskNumber, TaskTypeEnum taskTypeEnum);

    List<ReturnTaskDTO> getAllTasksByTaskType(TaskTypeEnum type);


    void taskRevision(AddTaskDTO currentTask);

    List<ReturnTaskDTO> getAll();

    void addNewRevision(List<AddTaskDTO> addTaskDTOs);

    List<AddTaskResponseDTO> addAllTasks(List<AddTaskDTO> addTaskDTOs);

    List<ReturnTaskDTO> getAllTasksWithoutOk(CheckTaskStatusDTO checkTaskStatusDTO);

    List<ReturnTaskDTO> updateAllTasks(List<UpdateTaskDTO> updateTaskDTOS);
}
