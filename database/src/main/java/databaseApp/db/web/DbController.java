package databaseApp.db.web;

import databaseApp.db.model.dto.DbAddTaskDTO;
import databaseApp.db.service.DbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/db")
public class DbController {


    private final DbService dbService;

    public DbController(DbService dbService) {
        this.dbService = dbService;
    }

    @PostMapping("/add/task")
    public ResponseEntity<String > addTask(@RequestBody DbAddTaskDTO dbAddTaskDTO) {

       /* if (dbService.existByTaskNumberAndType(dbAddTaskDTO.getTaskNumber(), dbAddTaskDTO.getType().name())){
           return new ResponseEntity<>("Task number for " + dbAddTaskDTO.getType().name() + "already exist!",HttpStatus.BAD_REQUEST);
        }*/

        dbService.addTask(dbAddTaskDTO);


        return new ResponseEntity<>("Task created successfully!", HttpStatus.CREATED);
    }

}
