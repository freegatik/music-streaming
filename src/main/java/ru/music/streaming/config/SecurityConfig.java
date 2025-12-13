package ru.music.streaming.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import ru.music.streaming.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/api/**")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/api/auth/me").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/playlists/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists/user/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists/{id}/tracks").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists/search").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/artists").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/albums").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tracks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tracks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/tracks/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                
                .requestMatchers("/api/artists/**").permitAll()
                .requestMatchers("/api/albums/**").permitAll()
                .requestMatchers("/api/tracks/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/playlists").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/playlists/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/playlists/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/playlists/{playlistId}/tracks").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/playlists/{playlistId}/tracks/move").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/playlists/{playlistId}/shuffle").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/playlists/{playlistId}/clone").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/playlists/{playlistId}/tracks/{position}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/playlists/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/playlists/{id}/tracks").permitAll()
                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response,
                                       org.springframework.security.core.AuthenticationException authException)
                            throws IOException {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        String message = authException.getMessage() != null ? 
                            escapeJson(authException.getMessage()) : "Требуется аутентификация";
                        response.getWriter().write(
                            "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
                            "\"status\":401," +
                            "\"error\":\"Ошибка аутентификации\"," +
                            "\"message\":\"" + message + "\"}"
                        );
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response,
                                     org.springframework.security.access.AccessDeniedException accessDeniedException)
                            throws IOException {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        String message = accessDeniedException.getMessage() != null ? 
                            escapeJson(accessDeniedException.getMessage()) : 
                            "У вас нет прав для выполнения этого действия";
                        response.getWriter().write(
                            "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
                            "\"status\":403," +
                            "\"error\":\"Доступ запрещён\"," +
                            "\"message\":\"" + message + "\"}"
                        );
                    }
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> {});
        
        return http.build();
    }
    
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

