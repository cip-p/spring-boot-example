package io.cip.services.cake.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return http.build();
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            PasswordEncoder passwordEncoder,
            @Value("${cakes.authentication.username}") String userName,
            @Value("${cakes.authentication.password}") String password
    ) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(userName)
                .password(passwordEncoder.encode(password))
                .authorities("ROLE_USER");
    }
}
