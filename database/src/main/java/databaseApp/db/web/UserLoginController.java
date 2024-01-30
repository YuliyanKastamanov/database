package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping
public class UserLoginController {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserLoginController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("users/login")
    public ResponseEntity<String> authenticateUser(@RequestBody UserLoginDTO userLoginDTO) {

        if (!userService.existByUNumber(userLoginDTO.getuNumber())) {
            return new ResponseEntity<>("Please provide correct U-number exist!", HttpStatus.BAD_REQUEST);
        }


        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getuNumber(), userLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User login successfully!...", HttpStatus.OK);
    }

}
