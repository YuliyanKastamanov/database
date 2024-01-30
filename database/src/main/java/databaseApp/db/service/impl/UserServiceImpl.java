package databaseApp.db.service.impl;

import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.model.event.UserRegisteredEvent;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.RoleService;
import databaseApp.db.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher appEventPublisher;



    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, RoleService roleService, PasswordEncoder passwordEncoder, ApplicationEventPublisher appEventPublisher) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;

        this.appEventPublisher = appEventPublisher;
    }


    @Override
    public boolean existByUNumber(String uNumber) {

        if(userRepository.findByuNumber(uNumber).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean userSignup(UserSignupDTO userSignupDTO) {

        if (!userSignupDTO.getPassword().equals(userSignupDTO.getConfirmPassword())){

            return false;
        }

        UserEntity user = modelMapper.map(userSignupDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userSignupDTO.getPassword()));
        RoleEntity role = roleService.findByName(userSignupDTO.getRole());
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        this.userRepository.save(user);
        appEventPublisher.publishEvent(new UserRegisteredEvent(
                "UserService",
                userSignupDTO.getEmail(),
                userSignupDTO.getName()
        ));
        return true;
    }


}
