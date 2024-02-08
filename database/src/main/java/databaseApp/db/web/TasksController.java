package databaseApp.db.web;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/tasks")
public class TasksController {


    private final TaskService taskService;

    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<String > addTask(@RequestBody AddTaskDTO addTaskDTO) {

        if (taskService.existByCri(addTaskDTO.getTaskNumber(), addTaskDTO.getType())){
           return new ResponseEntity<>(
                   "Task number:" + addTaskDTO.getTaskNumber() + " for " + addTaskDTO.getType().name() + " already exist!",HttpStatus.BAD_REQUEST);
        }

        taskService.addTask(addTaskDTO);


        return new ResponseEntity<>("Task created successfully!", HttpStatus.CREATED);
    }

}
