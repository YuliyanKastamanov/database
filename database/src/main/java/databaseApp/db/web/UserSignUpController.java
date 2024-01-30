package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.service.UserService;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@CrossOrigin("*")
@RestController
@RequestMapping
public class UserSignUpController {

    private final UserService userService;



    public UserSignUpController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping("/users/signup")
    public ResponseEntity<UserSignupDTO> registerUser(@RequestBody UserSignupDTO userSignupDTO,
                                                      UriComponentsBuilder uriComponentsBuilder) {

        //check if UNumber already exist
        if (userService.existByUNumber(userSignupDTO.getuNumber())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(userService.existsByEmail(userSignupDTO.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        userService.userSignup(userSignupDTO);


        return ResponseEntity.created(
                uriComponentsBuilder.path("/user/{id}").build(userSignupDTO)
        ).build();
    }




}
