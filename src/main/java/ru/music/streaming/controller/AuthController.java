package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.dto.RegistrationRequest;
import ru.music.streaming.dto.RegistrationResponse;
import ru.music.streaming.model.User;
import ru.music.streaming.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
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
}

