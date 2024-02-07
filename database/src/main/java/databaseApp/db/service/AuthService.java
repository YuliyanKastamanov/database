package databaseApp.db.service;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserSignupDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;


public interface AuthService {
    boolean existByUNumber(String s);

    boolean existsByEmail(String email);

    boolean userSignup(UserSignupDTO userSignupDTO);

    //boolean usersSignup(List<UserSignupDTO> userSignupDTOs);

    String login(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response);

}
