package databaseApp.db.config;

import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.impl.DbUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(

                // define which urls are visible by which users
                authorizeRequest -> authorizeRequest
                        //all static resources are situated in (folders name) are available for anyone
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        //Allow anyone to see the home page and login page and signup page
                        .requestMatchers("/", "/users/login", "/users/signup", "/users/login-error").permitAll()
                        // all other request are authenticated
                        .anyRequest().authenticated()


        ).formLogin(
                formLogin -> {
                    formLogin
                            //redirect here when we access something which is not allowed.
                            //this is the page where we perform login
                            .loginPage("/users/login")

                            //The names of the input fields
                            .usernameParameter("uNumber")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/")
                            .failureForwardUrl("/users/login-error");
                    //.failureForwardUrl("/users/login-error");
                }
        ).logout(
                logout -> {

                    logout
                            //the url where we should POST something in order to perform logout
                            .logoutUrl("/users/logout")
                            //where to go when logged out
                            .logoutSuccessUrl("/")
                            //invalidate the HTTP session
                            .invalidateHttpSession(true);
                }
        ).csrf(AbstractHttpConfigurer::disable);

        //TODO remember me!
        return httpSecurity.build();
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
}
