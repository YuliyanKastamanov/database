package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserRegisterDTO;
import databaseApp.db.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin()
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<String > registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) {

        //check if UNumber already exist
        if (authService.existByUNumber(userRegisterDTO.getuNumber())) {
            return new ResponseEntity<>("U-number already exists!", HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(authService.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);

        }

        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            return new ResponseEntity<>("Password and confirm password should be the same!",HttpStatus.BAD_REQUEST);
        }


        authService.userRegister(request, userRegisterDTO);



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



    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
    }

}
