package databaseApp.db.service.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class ActivationLinkCleanUpScheduler {


    @Scheduled(cron = "0 0 * * * */6")
    public void cleanUp(){

        System.out.println("Triggered now" + LocalDate.now());


    }
}
