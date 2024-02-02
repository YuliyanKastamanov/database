package databaseApp.db.config;

import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.impl.DbUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

/*    private final String rememberMeKey;

    public SecurityConfiguration(@Value("${databaseApp.remember.me.key}") String rememberMeKey) {
        this.rememberMeKey = rememberMeKey;
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(

                        // define which urls are visible by which users
                        authorizeRequest -> authorizeRequest
                                //all static resources are situated in (folders name) are available for anyone
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                //Allow anyone to see the home page and login page and signup page
                                .requestMatchers("/users/login").permitAll()
                                .requestMatchers("/users/signup").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/db/review").hasRole(RoleEnum.ADMIN.name())
                                // all other request are authenticated
                                .anyRequest().authenticated()


                ).csrf(AbstractHttpConfigurer::disable)
                .build();



    }


    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {

        // This service translates between "DB-APP" users and roles
        //to representation which spring security understands.
        return new DbUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }




}
