package databaseApp.db.service;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserRegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthService {
    boolean existByUNumber(String s);

    boolean existsByEmail(String email);

    boolean userRegister(HttpServletRequest request, UserRegisterDTO userRegisterDTO);

    //boolean usersSignup(List<UserSignupDTO> userSignupDTOs);

    String login(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response);

    void userRegister(UserRegisterDTO userRegisterDTO);
}
