package com.creditmodule.loanmanagementapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build()
        );
        manager.createUser(
                User.withUsername("customer")
                        .password("{noop}customer123")
                        .roles("CUSTOMER")
                        .build()
        );
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger erişimi serbest
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Admin işlemleri
                        .requestMatchers("/api/customers/**").hasRole("ADMIN")

                        // Customer işlemleri
                        .requestMatchers("/api/loans/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/installments/**").hasRole("CUSTOMER")

                        // Diğer tüm istekler kimlik doğrulaması ister
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
