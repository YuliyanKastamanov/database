package databaseApp.db.web;

import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
public class UserSignupController {

    private final UserService userService;

    public UserSignupController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String message() {
        return "---------Hello Spring Boot App------------";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserSignupDTO userSignupDTO) {




        //check if UNumber already exist
        if (userService.existByUNumber(userSignupDTO.getuNumber())) {
            return new ResponseEntity<>("Username is already exist!", HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(userService.existsByEmail(userSignupDTO.getEmail())) {
            return new ResponseEntity<>("Email is already exist!", HttpStatus.BAD_REQUEST);

        }

        userService.userSignup(userSignupDTO);


        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
    }


}
