package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserRegisterDTO;
import databaseApp.db.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<String > registerUser(@RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) {

        //check if UNumber already exist
        if (authService.existByUNumber(userRegisterDTO.getuNumber())) {
            return new ResponseEntity<>("U-number already exist!", HttpStatus.BAD_REQUEST);
        }


        //check if email already exist
        if(authService.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>("Email already exist!", HttpStatus.BAD_REQUEST);

        }

        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            return new ResponseEntity<>("Password and confirm password should be the same!",HttpStatus.BAD_REQUEST);
        }


        authService.userRegister(request, userRegisterDTO);



        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

/*
    @PostMapping("/signup/add")
    public ResponseEntity<String > registerUsers(@RequestBody List<UserSignupDTO> userSignupDTOs) {


        for (UserSignupDTO userSignupDTO : userSignupDTOs) {
            //check if UNumber/s already exist
            if (authService.existByUNumber(userSignupDTO.getuNumber())) {
                return new ResponseEntity<>("Provided u-number/s already exist!",HttpStatus.BAD_REQUEST);
            }
            //Password and confirm password not the same!
            if (!userSignupDTO.getPassword().equals(userSignupDTO.getConfirmPassword())){
                return new ResponseEntity<>("Password and confirm password are not the same!",HttpStatus.BAD_REQUEST);
            }
            //check if email/s already exist
            if (authService.existsByEmail(userSignupDTO.getEmail())) {
                return new ResponseEntity<>("Provided email/s already exist!", HttpStatus.BAD_REQUEST);

            }
        }


        authService.usersSignup(userSignupDTOs);


        return new ResponseEntity<>("Users registered successfully!", HttpStatus.CREATED);
    }
*/



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
