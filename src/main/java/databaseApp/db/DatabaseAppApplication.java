package databaseApp.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DatabaseAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseAppApplication.class, args);
	}

}
