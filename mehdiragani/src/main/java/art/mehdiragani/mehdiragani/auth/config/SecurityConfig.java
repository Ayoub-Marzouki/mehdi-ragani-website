package art.mehdiragani.mehdiragani.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import art.mehdiragani.mehdiragani.auth.services.UserService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    // to encode passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Lock down URLs and wire in form‑login
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .userDetailsService(userService)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("Admin")
            .anyRequest().permitAll()
        )
        .formLogin(form -> form
            .loginPage("/user/login")
            .loginProcessingUrl("/user/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/user/logout")
            .logoutSuccessUrl("/")
        );
        return http.build();
    }
}