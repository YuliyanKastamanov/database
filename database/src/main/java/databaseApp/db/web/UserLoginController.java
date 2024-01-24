package databaseApp.db.web;

import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/users/login")
public class UserLoginController {


    @GetMapping
    public String message() {
        return "---------Hello Spring Boot App------------";
    }

    @GetMapping ("-error")
    public String errorMessage(){
        return "---------Error------------";
    }

}
