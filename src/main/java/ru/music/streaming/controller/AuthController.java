package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.dto.LoginRequest;
import ru.music.streaming.dto.RefreshRequest;
import ru.music.streaming.dto.RegistrationRequest;
import ru.music.streaming.dto.RegistrationResponse;
import ru.music.streaming.dto.TokenResponse;
import ru.music.streaming.model.User;
import ru.music.streaming.model.UserSession;
import ru.music.streaming.security.SecurityUser;
import ru.music.streaming.service.TokenService;
import ru.music.streaming.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    public AuthController(UserService userService, TokenService tokenService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        User user = userService.registerUser(request);
        
        RegistrationResponse response = new RegistrationResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();

        String deviceId = request.getUsername() + "_" + System.currentTimeMillis();
        UserSession session = tokenService.createSession(user, deviceId);

        TokenResponse response = new TokenResponse(session.getAccessToken(), session.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        UserSession newSession = tokenService.refreshTokens(request.getRefreshToken());

        TokenResponse response = new TokenResponse(newSession.getAccessToken(), newSession.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

