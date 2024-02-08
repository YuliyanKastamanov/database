package databaseApp.db.init;


import databaseApp.db.service.TaskTypeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbTypeInitializer implements CommandLineRunner {


    private final TaskTypeService taskTypeService;

    public DbTypeInitializer(TaskTypeService taskTypeService) {
        this.taskTypeService = taskTypeService;
    }

    @Override
    public void run(String... args) throws Exception {

        taskTypeService.initDbTypes();

    }


}
