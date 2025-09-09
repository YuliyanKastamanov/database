package databaseApp.db.config;

import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.UserRepository;
import databaseApp.db.service.impl.UserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {


/*    private final String rememberMeKey;

    public SecurityConfiguration(@Value("${databaseApp.remember.me.key}") String rememberMeKey) {
        this.rememberMeKey = rememberMeKey;
    }*/



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        // Статични ресурси
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // React SPA static files (само frontend-а да може да се сервира)
                        .requestMatchers(
                                "/", "/index.html",
                                "/static/**", "/assets/**", "/favicon.ico",
                                "/login", "/dashboard", "/task-status", "/generator", "/reports",
                                "/manage-users","/manage-users/**",
                                "/manage-tasks", "/manage-tasks/**"
                        ).permitAll()

                        // Auth API
                        .requestMatchers("/auth/login").permitAll()

                        // Registration restricted to ADMIN
                        .requestMatchers("/auth/register").hasRole(RoleEnum.ADMIN.name())

                        // Tasks API (admin)
                        .requestMatchers(HttpMethod.POST, "/tasks").hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/tasks/revisions").hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/tasks").hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/tasks").hasRole(RoleEnum.ADMIN.name())

                        // Normal users can check status
                        .requestMatchers(HttpMethod.POST, "/tasks/status").authenticated()

                        // Example protected route
                        .requestMatchers("/test").hasRole(RoleEnum.ADMIN.name())

                        // Всичко друго иска автентикация
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                        .logoutSuccessUrl("/") // redirect след logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080")); // React dev server
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Allow cookies/auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
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
