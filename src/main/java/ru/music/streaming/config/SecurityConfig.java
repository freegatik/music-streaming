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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import ru.music.streaming.security.JwtAuthenticationFilter;

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
        // Настройка CSRF: отключаем для API
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
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless для JWT
            )
            .authorizeHttpRequests(auth -> auth
                // Публичные эндпоинты
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> {}); // Оставляем Basic Auth для обратной совместимости
        
        return http.build();
    }
}

