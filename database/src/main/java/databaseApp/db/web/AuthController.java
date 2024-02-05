package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.HttpStatus.OK;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class AuthController {


    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/signup")
    public ResponseEntity<String > registerUser(@RequestBody UserSignupDTO userSignupDTO,
                                                      UriComponentsBuilder uriComponentsBuilder) {

        //check if UNumber already exist
        if (authService.existByUNumber(userSignupDTO.getuNumber())) {
            return new ResponseEntity<>("U-number already exist!", HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(authService.existsByEmail(userSignupDTO.getEmail())) {
            return new ResponseEntity<>("Email already exist!", HttpStatus.BAD_REQUEST);

        }

        authService.userSignup(userSignupDTO);


        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(
            @Valid
            @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return new ResponseEntity<>(authService.login(userLoginDTO, request, response), OK);
    }





}
