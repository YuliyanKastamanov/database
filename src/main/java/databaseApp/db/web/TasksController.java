package databaseApp.db.web;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.model.dto.AddTaskResponseDTO;
import databaseApp.db.model.dto.CheckTaskStatusDTO;
import databaseApp.db.model.dto.ReturnTaskDTO;
import databaseApp.db.model.entity.enums.ResponseEnum;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/tasks")
public class TasksController {


    private final TaskService taskService;

    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<List<AddTaskResponseDTO>> addTasks(@RequestBody List<AddTaskDTO> addTaskDTOs) {

        List<AddTaskResponseDTO> results = taskService.addAllTasks(addTaskDTOs);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/add/rev")
    public ResponseEntity<List<ReturnTaskDTO>> addNewRevision(@RequestBody List<AddTaskDTO> addTaskDTOs) {

        List<ReturnTaskDTO> tasks = taskService.getAllTasksByTaskType(addTaskDTOs.get(0).getType());
        taskService.addNewRevision(addTaskDTOs);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/get/tasks/taskType")
    public ResponseEntity<List<ReturnTaskDTO>> getTasksByTaskType(@RequestBody TaskTypeEnum taskTypeEnum) {

        List<ReturnTaskDTO> tasks = taskService.getAllTasksByTaskType(taskTypeEnum);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<ReturnTaskDTO>> getAllTasks() {
        List<ReturnTaskDTO> tasks = taskService.getAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("check/status")
    public ResponseEntity<List<ReturnTaskDTO>> checkStatus(@RequestBody CheckTaskStatusDTO checkTaskStatusDTO) {
        List<ReturnTaskDTO> tasks = taskService.getAllTasksWithoutOk(checkTaskStatusDTO);
        return ResponseEntity.ok(tasks);
    }




}
