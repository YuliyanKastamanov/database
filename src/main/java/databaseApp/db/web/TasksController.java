package databaseApp.db.web;

import databaseApp.db.model.dto.*;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/tasks")
public class TasksController {


    private final TaskService taskService;

    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping()
    public ResponseEntity<List<AddTaskResponseDTO>> addTasks(@Valid @RequestBody List<AddTaskDTO> addTaskDTOs) {

        List<AddTaskResponseDTO> results = taskService.addAllTasks(addTaskDTOs);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/revisions")
    public ResponseEntity<List<ReturnTaskDTO>> addNewRevision( @Valid @RequestBody List<AddTaskDTO> addTaskDTOs) {

        List<ReturnTaskDTO> tasks = taskService.getAllTasksByTaskType(addTaskDTOs.get(0).getType());
        taskService.addNewRevision(addTaskDTOs);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping
    public ResponseEntity<List<ReturnTaskDTO>> getTasksByTaskType(@RequestParam(value = "type", required = false) TaskTypeEnum taskTypeEnum) {

        List<ReturnTaskDTO> tasks;
        if (taskTypeEnum != null) {
            tasks = taskService.getAllTasksByTaskType(taskTypeEnum);
        } else {
            tasks = taskService.getAll();
        }
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/status")
    public ResponseEntity<List<ReturnTaskDTO>> checkStatus(@Valid @RequestBody CheckTaskStatusDTO checkTaskStatusDTO) {
        List<ReturnTaskDTO> tasks = taskService.getAllTasksWithoutOk(checkTaskStatusDTO);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/update")
    public ResponseEntity<List<ReturnTaskDTO>> updateTasks(@Valid @RequestBody List<UpdateTaskDTO> updateTaskDTOS) {
        List<ReturnTaskDTO> tasks = taskService.updateAllTasks(updateTaskDTOS);
        return ResponseEntity.ok(tasks);
    }




}
