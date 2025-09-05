package databaseApp.db.service.impl;


import databaseApp.db.model.dto.PasswordDto;
import databaseApp.db.model.dto.UserRegisterDTO;
import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.AuthService;
import databaseApp.db.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.datatransfer.DataFlavor;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private static final String INCORRECT_OLD_PASSWORD = "Old password is incorrect!";
    private static final String NAME = "Yuliyan Kastamanov";
    private static final String EMAIL = "yuliyan.kastamanov@lht-sofia.com";
    private static final String PASSWORD = "welcome";
    private static final String U_NUMBER = "U656285";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final AuthService authService;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authManager, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.authService = authService;
    }


    @Override
    public UserEntity findByUNumber(String uNumber) {
        return userRepository.findByuNumberIgnoreCase(uNumber).orElse(null);
    }

    @Override
    public void changePassword(PasswordDto passwordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uNumber = authentication.getName();
        UserEntity user = findByUNumber(uNumber);

        // Check old password
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException(INCORRECT_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void initUser() {
        if(userRepository.count() == 0){
            UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
            userRegisterDTO.setName(NAME);
            userRegisterDTO.setEmail(EMAIL);
            userRegisterDTO.setPassword(PASSWORD);
            userRegisterDTO.setConfirmPassword(PASSWORD);
            userRegisterDTO.setuNumber(U_NUMBER);
            userRegisterDTO.setRole(RoleEnum.ADMIN);
            authService.userRegister(userRegisterDTO);

        }
    }
}
