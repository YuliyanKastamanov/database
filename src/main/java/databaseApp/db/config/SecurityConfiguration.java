package databaseApp.db.config;

import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.impl.UserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;




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
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/auth/register").permitAll()
                                    .requestMatchers("test").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/tasks/add").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/tasks/add/rev").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/get/tasks/taskType").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/get/all").hasRole(RoleEnum.ADMIN.name())
                                .requestMatchers("/tasks/review").hasRole(RoleEnum.ADMIN.name())
                                // all other request are authenticated
                                .anyRequest().authenticated()


                ).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession) //

                ).logout(
                        logout -> {
                            logout
                                    // the URL where we should POST something in order to perform the logout
                                    .logoutUrl("/auth/logout")
                                    // where to go when logged out?
                                    .logoutSuccessUrl("/")
                                    // invalidate the HTTP session
                                    .invalidateHttpSession(true);
                        }
                )
                .build();





    }


    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService(UserRepository userRepository) {

        // This service translates between "DB-APP" users and roles
        //to representation which spring security understands.
        return new UserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    /** A SecurityContextRepository implementation which stores the security context in the HttpSession between requests. */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


}
