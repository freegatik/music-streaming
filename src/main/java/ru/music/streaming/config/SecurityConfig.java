package ru.music.streaming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Настройка CSRF: отключаем для API (Basic Auth), но оставляем cookie-based для будущего использования
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/api/**") // Отключаем CSRF для всех API эндпоинтов
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless для Basic Auth
            )
            .authorizeHttpRequests(auth -> auth
                // Публичные эндпоинты
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/playlists/public").permitAll() // Публичные плейлисты
                
                // Административные операции для артистов, альбомов, треков (должны быть ПЕРЕД общими правилами)
                .requestMatchers(HttpMethod.POST, "/api/artists").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/albums").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tracks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tracks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/tracks/**").hasRole("ADMIN")
                
                // GUEST доступ для чтения (GET) артистов, альбомов, треков
                .requestMatchers("/api/artists/**").permitAll()
                .requestMatchers("/api/albums/**").permitAll()
                .requestMatchers("/api/tracks/**").permitAll()
                
                // Плейлисты: USER может создавать/изменять свои, ADMIN - все
                // Детальная проверка владельца будет в контроллере
                .requestMatchers("/api/playlists/**").hasAnyRole("USER", "ADMIN")
                
                // Пользователи: USER видит только себя, ADMIN - всех
                // Детальная проверка будет в контроллере
                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {}); // Включаем Basic Authentication
        
        return http.build();
    }
}

