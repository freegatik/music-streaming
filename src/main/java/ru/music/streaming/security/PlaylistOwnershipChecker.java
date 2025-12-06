package ru.music.streaming.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.Role;
import ru.music.streaming.model.User;

@Component
public class PlaylistOwnershipChecker {
    
    public boolean isOwnerOrAdmin(Playlist playlist) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SecurityUser)) {
            return false;
        }
        
        SecurityUser securityUser = (SecurityUser) principal;
        User currentUser = securityUser.getUser();
        
        if (currentUser == null) {
            return false;
        }
        
        if (currentUser.getRole() == Role.ADMIN) {
            return true;
        }
        
        if (playlist.getUser() == null) {
            return false;
        }
        return playlist.getUser().getId().equals(currentUser.getId());
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SecurityUser)) {
            return null;
        }
        
        SecurityUser securityUser = (SecurityUser) principal;
        return securityUser.getUser();
    }
    
    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}

