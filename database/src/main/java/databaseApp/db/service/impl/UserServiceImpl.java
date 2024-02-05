package databaseApp.db.service.impl;

import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }





}
