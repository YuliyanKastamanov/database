package databaseApp.db.service.impl;

import databaseApp.db.model.dto.UserSignupDTO;
import databaseApp.db.model.entity.Role;
import databaseApp.db.model.entity.User;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean existByUNumber(String s) {

        if(userRepository.findByuNumber(s)){
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        if(userRepository.findByEmail(email)){
            return true;
        }
        return false;
    }

    @Override
    public boolean userSignup(UserSignupDTO userSignupDTO) {

        if (!userSignupDTO.getPassword().equals(userSignupDTO.getConfirmPassword())){

            return false;
        }

        User user = modelMapper.map(userSignupDTO, User.class);

        user.setRole(userRepository.findByRoleEnum(userSignupDTO.getRoleEnum()));

        this.userRepository.save(user);
        return true;
    }
}
