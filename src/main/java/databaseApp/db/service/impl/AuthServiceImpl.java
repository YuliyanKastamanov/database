package databaseApp.db.service.impl;

import databaseApp.db.model.dto.UserLoginDTO;
import databaseApp.db.model.dto.UserRegisterDTO;
import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.event.UserRegisteredEvent;
import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.RoleService;
import databaseApp.db.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;

    private final ApplicationEventPublisher appEventPublisher;

    private final SecurityContextRepository securityContextRepository;


    public AuthServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authManager,
                           ApplicationEventPublisher appEventPublisher,
                           SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.appEventPublisher = appEventPublisher;
        this.securityContextRepository = securityContextRepository;
    }


    @Override
    public boolean existByUNumber(String uNumber) {

        if (userRepository.findByuNumber(uNumber).isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean userRegister(HttpServletRequest request, UserRegisterDTO userRegisterDTO) {


        UserEntity user = modelMapper.map(userRegisterDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        RoleEntity role = roleService.findByName(userRegisterDTO.getRole());
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(role);
        if(role.getRole().equals(RoleEnum.ADMIN)){
            RoleEntity roleUser = new RoleEntity();
            roleUser.setRole(RoleEnum.USER);
            roles.add(roleUser);
        }
        user.setRoles(roles);

        this.userRepository.save(user);
        appEventPublisher.publishEvent(new UserRegisteredEvent(
                "UserService",
                userRegisterDTO.getEmail(),
                userRegisterDTO.getName()
        ));

        //appEventPublisher.publishEvent(new UserRegisteredEvent(user,userSignupDTO.getEmail(), userSignupDTO.getuNumber()));
        return true;
    }
    @Override
    public void userRegister(UserRegisterDTO userRegisterDTO) {
        UserEntity user = modelMapper.map(userRegisterDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        RoleEntity role = roleService.findByName(userRegisterDTO.getRole());
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(role);
        if(role.getRole().equals(RoleEnum.ADMIN)){
            RoleEntity roleUser = roleService.findByName(RoleEnum.USER);
            roles.add(roleUser);
        }
        user.setRoles(roles);

        this.userRepository.save(user);

    }



/*
    @Override
    public boolean usersSignup(List<UserSignupDTO> userSignupDTOs) {

        userSignupDTOs.forEach(userSignupDTO -> {

            UserEntity user = modelMapper.map(userSignupDTO, UserEntity.class);
            user.setPassword(passwordEncoder.encode(userSignupDTO.getPassword()));
            RoleEntity role = roleService.findByName(userSignupDTO.getRole());
            List<RoleEntity> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            this.userRepository.save(user);

        });


        return true;
    }
*/



    @Override
    public String login(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                userLoginDTO.getuNumber(), userLoginDTO.getPassword()));

        // Create a new context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // Update SecurityContextHolder
        this.securityContextRepository.saveContext(context, request, response);

        return "Logged In!";
    }


}
