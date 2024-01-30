package databaseApp.db.service.impl;

import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;

public class DbUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String uNumber) throws UsernameNotFoundException {

        return userRepository
                .findByuNumber(uNumber)
                .map(DbUserDetailsService::map)
                .orElseThrow(() -> new UsernameNotFoundException("User " + uNumber + " not found!"));

    }

    private static UserDetails map(UserEntity userEntity) {

        return User
                .withUsername(userEntity.getuNumber())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRoles().stream().map(DbUserDetailsService::map).toList() )
                .build();


    }

    private static GrantedAuthority map(RoleEntity roleEntity) {

        return new SimpleGrantedAuthority(
                "ROLE_" + roleEntity.getRole().name()
        );

    }




}
