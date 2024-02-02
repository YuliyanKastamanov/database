package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class AuthController {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/signup")
    public ResponseEntity<String > registerUser(@RequestBody UserSignupDTO userSignupDTO,
                                                      UriComponentsBuilder uriComponentsBuilder) {

        //check if UNumber already exist
        if (userService.existByUNumber(userSignupDTO.getuNumber())) {
            return new ResponseEntity<>("U-number already exist!", HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(userService.existsByEmail(userSignupDTO.getEmail())) {
            return new ResponseEntity<>("Email already exist!", HttpStatus.BAD_REQUEST);

        }

        userService.userSignup(userSignupDTO);


        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody UserLoginDTO userLoginDTO) {

        if (!userService.existByUNumber(userLoginDTO.getuNumber())) {
            return new ResponseEntity<>("Please provide correct U-number!", HttpStatus.BAD_REQUEST);
        }



        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getuNumber(), userLoginDTO.getPassword());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Set the authentication in the SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate and return a token or any other response
            //String token = generateToken(userDetails.loadUserByUsername(userLoginDTO.getuNumber()).getUsername());
            return ResponseEntity.ok("Login successfully!");

        } catch (AuthenticationException e) {
            // Authentication failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}
