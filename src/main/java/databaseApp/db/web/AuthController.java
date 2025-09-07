package databaseApp.db.web;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserLoginResponseDTO;
import databaseApp.db.model.dto.UserRegisterDTO;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDTO dto,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            String msg = authService.login(dto, request, response);
            UserEntity user = authService.findByUNumber(dto.getuNumber());
            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getRole().name())
                    .toList();

            return ResponseEntity.ok(new UserLoginResponseDTO(
                    user.getuNumber(),
                    user.getEmail(),
                    user.getName(),   // <-- вече връщаме и името
                    roles,
                    msg
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(Map.of("message", ex.getMessage()));
        }
    }

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String username = authentication.getName(); // това е UNumber
        UserEntity user = authService.findByUNumber(username);

        List<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .toList();

        return ResponseEntity.ok(
                new UserLoginResponseDTO(user.getuNumber(), user.getEmail(),user.getName(), roles, "Authenticated")
        );
    }


    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
    }

}
