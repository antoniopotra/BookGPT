package software.design.gamegpt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security configuration class
 * features: password encoding, endpoint protection against unauthorized access
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/deleteUser/**").hasRole("ADMIN")
                        .requestMatchers("/upgrade").hasRole("USER")
                        .requestMatchers("/confirm-upgrade").hasRole("USER")
                        .requestMatchers("/game/**").hasRole("USER")
                        .requestMatchers("/handlePlay/**").hasRole("USER")
                        .requestMatchers("/handleLike/**").hasRole("USER")
                        .requestMatchers("/played").hasRole("USER")
                        .requestMatchers("/liked").hasRole("USER")
                        .requestMatchers("/search").hasRole("USER")
                        .requestMatchers("/recommendations").hasRole("USER")
                        .requestMatchers("/index").authenticated()
                        .requestMatchers("/stats").authenticated()
                        .requestMatchers("/register/**").permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/index")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll())
                .exceptionHandling(exception -> exception.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Custom error handler, it redirects to the index page if the user is not authorized to view an endpoint
     */
    @Component
    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }
}