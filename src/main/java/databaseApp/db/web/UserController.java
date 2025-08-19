package databaseApp.db.web;


import databaseApp.db.model.dto.PasswordDto;
import databaseApp.db.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static final String PASSWORD_CONFIRMED_PASSWORD_NOT_SAME = "New password and confirmed new password should be the same!";
    private static final String SUCCESSFULLY_CHANGED = "Password changed successfully!";
    private final UserService userService;



    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/change-password")
    public ResponseEntity<String > changePassword(@RequestBody PasswordDto passwordDto){


        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            return new ResponseEntity<>(PASSWORD_CONFIRMED_PASSWORD_NOT_SAME, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.changePassword(passwordDto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(SUCCESSFULLY_CHANGED, HttpStatus.OK);
    }
}
