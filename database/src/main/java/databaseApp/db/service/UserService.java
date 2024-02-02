package databaseApp.db.service;

import databaseApp.db.model.dto.UserSignupDTO;
import org.springframework.security.core.Authentication;

public interface UserService {
    boolean existByUNumber(String s);

    boolean existsByEmail(String email);

    boolean userSignup(UserSignupDTO userSignupDTO);


}
