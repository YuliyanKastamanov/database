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


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/change-password")
    public ResponseEntity<String > changePassword(@RequestBody PasswordDto passwordDto){


        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())){
            return new ResponseEntity<>("New password and confirmed new password should be the same!", HttpStatus.BAD_REQUEST);
        }

        userService.changePassword(passwordDto);


        return new ResponseEntity<>("Password changed successfully!", HttpStatus.OK);
    }
}
