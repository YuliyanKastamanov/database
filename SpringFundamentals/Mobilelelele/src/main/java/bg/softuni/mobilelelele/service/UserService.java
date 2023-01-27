package bg.softuni.mobilelelele.service;

import bg.softuni.mobilelelele.model.dto.UserLoginDTO;
import bg.softuni.mobilelelele.model.entity.UserEntity;
import bg.softuni.mobilelelele.repository.UserRepository;
import bg.softuni.mobilelelele.user.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    private Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    private CurrentUser currentUser;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, CurrentUser currentUser,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;

        this.passwordEncoder = passwordEncoder;
    }



    public boolean login(UserLoginDTO userLoginDTO){

       Optional<UserEntity> user =  userRepository.findByEmail(userLoginDTO.getUsername());

       if(user.isEmpty()){
           LOGGER.info("User with name [{}] not found.", userLoginDTO.getUsername());
           return false;
       }

       var rawPassword = userLoginDTO.getPassword();
       var encodedPassword = user.get().getPassword();

       boolean success = passwordEncoder.matches(rawPassword, encodedPassword );

       if (success){
           login(user.get());
       }else {
           logout();

       }

       return success;
    }

    private void login(UserEntity userEntity){
        currentUser
                .setLoggedIn(true)
                .setName(userEntity.getFirstName() + " " + userEntity.getLastName());
    }

    public void logout(){
        currentUser.clear();
    }
}
