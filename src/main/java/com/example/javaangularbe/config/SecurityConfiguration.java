package com.example.javaangularbe.config;


import com.example.javaangularbe.JwtTokenFilter;
import com.example.javaangularbe.user.Role;
import com.example.javaangularbe.user.User;
import com.example.javaangularbe.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final JwtTokenFilter jwtTokenFilter;

    //TODO REFACTOR
    private static final String ROLE_PREFIX = "ROLE_";

    @EventListener(ApplicationReadyEvent.class)
    private void saveUser() {
        userRepository.deleteAll();
        userRepository.save(new User("qbox2010@gmail.com", getBcryptPasswordEncoder().encode("password123"), ROLE_PREFIX + Role.USER.getValue()));
        userRepository.save(new User("ania@gmail.com", getBcryptPasswordEncoder().encode("password123"), ROLE_PREFIX + Role.ADMIN.getValue()));
    }

    @Bean
    public PasswordEncoder getBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with name: " + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/hello").hasRole(Role.ADMIN.getValue())
                    .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecurityConfigurer ->
                        httpSecurityConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
