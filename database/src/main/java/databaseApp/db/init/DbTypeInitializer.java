package databaseApp.db.init;


import databaseApp.db.service.DbTypeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbTypeInitializer implements CommandLineRunner {


    private final DbTypeService dbTypeService;

    public DbTypeInitializer(DbTypeService dbTypeService) {
        this.dbTypeService = dbTypeService;
    }

    @Override
    public void run(String... args) throws Exception {

        dbTypeService.initDbTypes();

    }


}
