package databaseApp.db.web;

import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.service.TaskTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/task-types")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    public TaskTypeController(TaskTypeService taskTypeService) {
        this.taskTypeService = taskTypeService;
    }

    @GetMapping
    public List<TaskTypeEntity> getAllTaskTypes() {
        return taskTypeService.getAll();
    }

}
