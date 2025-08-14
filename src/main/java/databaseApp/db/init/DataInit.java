package databaseApp.db.init;

import databaseApp.db.service.RoleService;
import databaseApp.db.service.TaskTypeService;
import databaseApp.db.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    private final TaskTypeService taskTypeService;
    private final RoleService roleService;

    private final UserService userService;

    public DataInit(TaskTypeService taskTypeService, RoleService roleService, UserService userService) {
        this.taskTypeService = taskTypeService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        taskTypeService.initDbTypes();
        roleService.initRoles();
        userService.initUser();

    }
}
