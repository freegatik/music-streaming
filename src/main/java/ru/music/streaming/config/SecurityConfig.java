package ru.music.streaming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .requestMatchers("/api/artists/**").permitAll() // GUEST доступ
                .requestMatchers("/api/albums/**").permitAll() // GUEST доступ
                .requestMatchers("/api/tracks/**").permitAll() // GUEST доступ
                .requestMatchers("/api/playlists/public").permitAll() // Публичные плейлисты
                
                // Административные операции для артистов, альбомов, треков
                .requestMatchers("/api/artists").hasRole("ADMIN") // POST
                .requestMatchers("/api/artists/*").hasRole("ADMIN") // PUT, DELETE
                .requestMatchers("/api/albums").hasRole("ADMIN") // POST
                .requestMatchers("/api/albums/*").hasRole("ADMIN") // PUT, DELETE
                .requestMatchers("/api/tracks").hasRole("ADMIN") // POST
                .requestMatchers("/api/tracks/*").hasRole("ADMIN") // PUT, DELETE
                
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

