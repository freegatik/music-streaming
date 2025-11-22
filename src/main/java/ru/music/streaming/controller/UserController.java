package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.dto.DailyMixRequest;
import ru.music.streaming.dto.UserLibrarySummaryResponse;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.User;
import ru.music.streaming.security.PlaylistOwnershipChecker;
import ru.music.streaming.service.PlaylistService;
import ru.music.streaming.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final PlaylistService playlistService;
    private final PlaylistOwnershipChecker ownershipChecker;
    
    @Autowired
    public UserController(UserService userService, PlaylistService playlistService, PlaylistOwnershipChecker ownershipChecker) {
        this.userService = userService;
        this.playlistService = playlistService;
        this.ownershipChecker = ownershipChecker;
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.createUser(user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Только ADMIN может видеть всех пользователей
        if (!ownershipChecker.isAdmin()) {
            throw new AccessDeniedException("Только администратор может просматривать список всех пользователей");
        }
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // USER может видеть только себя, ADMIN - всех
        if (!ownershipChecker.isAdmin() && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Вы можете просматривать только свои данные");
        }
        
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                          @Valid @RequestBody User user) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // USER может изменять только себя, ADMIN - всех
        if (!ownershipChecker.isAdmin() && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Вы можете изменять только свои данные");
        }
        
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // USER может удалять только себя, ADMIN - всех
        if (!ownershipChecker.isAdmin() && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Вы можете удалять только свой аккаунт");
        }
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        User user = userService.getUserByEmail(email);
        
        // USER может видеть только себя, ADMIN - всех
        if (!ownershipChecker.isAdmin() && !currentUser.getEmail().equals(email)) {
            throw new AccessDeniedException("Вы можете просматривать только свои данные");
        }
        
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/{userId}/mix")
    public ResponseEntity<Playlist> createDailyMix(@PathVariable Long userId,
                                                   @Valid @RequestBody DailyMixRequest request) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // USER может создавать микс только для себя, ADMIN - для всех
        if (!ownershipChecker.isAdmin() && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Вы можете создавать микс только для себя");
        }
        
        Playlist playlist = playlistService.createDailyMix(userId, request);
        return new ResponseEntity<>(playlist, HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}/summary")
    public ResponseEntity<UserLibrarySummaryResponse> getUserSummary(@PathVariable Long userId) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // USER может видеть статистику только свою, ADMIN - всех
        if (!ownershipChecker.isAdmin() && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Вы можете просматривать статистику только свою");
        }
        
        UserLibrarySummaryResponse summary = userService.getUserLibrarySummary(userId);
        return ResponseEntity.ok(summary);
    }
}
