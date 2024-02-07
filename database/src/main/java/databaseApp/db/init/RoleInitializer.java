package databaseApp.db.init;

import databaseApp.db.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleService roleService;


    public RoleInitializer(RoleService roleService) {
        this.roleService = roleService;
    }


    @Override
    public void run(String... args) throws Exception {

        roleService.initRoles();

    }


}
