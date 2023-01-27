package bg.softuni.L1_Intro;

import bg.softuni.L1_Intro.model.dto.CreateOwnerDto;
import bg.softuni.L1_Intro.service.OwnerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CatDemo implements CommandLineRunner {

    private OwnerService ownerService;

    public CatDemo(OwnerService ownerService){

        this.ownerService = ownerService;
    }
    @Override
    public void run(String... args) throws Exception {
        ownerService.createOwner(new CreateOwnerDto()
                .setOwnerName("Pesho")
                .setCatNames(List.of("Chinchila", "Stoyan")));

    }
}
