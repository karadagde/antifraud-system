package antifraud.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    AuthenticationEntryPoint restAuthenticationEntryPoint;

    SecurityConfig(AuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                                .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("ROLE_MERCHANT")
                                .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority("ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers("/api/antifraud/suspicious-ip/**").hasAnyAuthority("ROLE_SUPPORT")
                                .requestMatchers("/api/antifraud/stolencard/**").hasAnyAuthority("ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasAnyAuthority("ROLE_SUPPORT")
                                .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                        // other matchers
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                );
        // other configurations
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
