package dss.practicas.practica_1.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //(debug = true)
public class SecurityConfig {

    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin").password("{noop}admin").roles("ADMIN").build(),
            User.withUsername("manager").password("{noop}manager").roles("MANAGER").build(),
            User.withUsername("user").password("{noop}user").roles("USER").build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Allow APIs without CSRF
            // .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Allow REST API (ALL METHODS: GET, POST, DELETE…)
                // .requestMatchers("/api/**").permitAll()

                // Static resources (css, js, images) must be reachable without login
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()

                // Public pages
                .requestMatchers("/", "/index", "/cart/**", "/error")
                    .permitAll()

                // Public product browsing (MVC)
                .requestMatchers(HttpMethod.GET, "/products").permitAll()
                
                // Manager and Admin pages
                .requestMatchers("/products/add", "/products/edit/**", 
                                "/products/save")
                    .hasAnyRole("ADMIN", "MANAGER")

                // Admin-only pages
                .requestMatchers("/admin/**", "/products/delete/**")
                    .hasRole("ADMIN")
                
                // H2 console
                .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN") 

                // Everything else requires login
                .anyRequest().authenticated()
            )

            // Browser login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll()
            );

        return http.build();
    }
}