package databaseApp.db.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
                        .requestMatchers("/", "users/login", "/users/signup").permitAll()
                        // all other request are authenticated
                        .anyRequest().authenticated()


        ).formLogin(
                formLogin -> {
                    formLogin
                            //redirect here when we access something which is not allowed.
                            //this is the page where we perform login
                            .loginPage("users/login")

                            //The names of the input fields
                            .usernameParameter("uNumber")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/")
                            .failureForwardUrl("/users/login-error");
                }
        ).logout(
                logout -> {

                    logout
                            .logoutUrl("/users/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true);
                }
        );

        //TODO remember me!
        return httpSecurity.build();
    }
}
