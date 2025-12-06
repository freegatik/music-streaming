package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.dto.PlaylistCloneRequest;
import ru.music.streaming.dto.PlaylistMoveRequest;
import ru.music.streaming.dto.PlaylistTrackResponse;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.security.PlaylistOwnershipChecker;
import ru.music.streaming.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    
    private final PlaylistService playlistService;
    private final PlaylistOwnershipChecker ownershipChecker;
    
    @Autowired
    public PlaylistController(PlaylistService playlistService, PlaylistOwnershipChecker ownershipChecker) {
        this.playlistService = playlistService;
        this.ownershipChecker = ownershipChecker;
    }
    
    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@Valid @RequestBody Playlist playlist) {
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        Playlist created = playlistService.createPlaylist(playlist, currentUser.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        var currentUser = ownershipChecker.getCurrentUser();
        List<Playlist> playlists;
        
        if (currentUser == null) {
            playlists = playlistService.getPublicPlaylists();
        } else if (ownershipChecker.isAdmin()) {
            playlists = playlistService.getAllPlaylists();
        } else {
            playlists = playlistService.getAllPlaylists().stream()
                    .filter(p -> p.getIsPublic() || p.getUser().getId().equals(currentUser.getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        var currentUser = ownershipChecker.getCurrentUser();
        
        if (!playlist.getIsPublic() && (currentUser == null || 
            (!ownershipChecker.isAdmin() && !playlist.getUser().getId().equals(currentUser.getId())))) {
            throw new AccessDeniedException("У вас нет доступа к этому плейлисту");
        }
        
        return ResponseEntity.ok(playlist);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id,
                                                   @Valid @RequestBody Playlist playlist) {
        Playlist existing = playlistService.getPlaylistById(id);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для изменения этого плейлиста");
        }
        Playlist updated = playlistService.updatePlaylist(id, playlist);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        Playlist existing = playlistService.getPlaylistById(id);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для удаления этого плейлиста");
        }
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getPlaylistsByUser(@PathVariable Long userId) {
        var currentUser = ownershipChecker.getCurrentUser();
        List<Playlist> playlists = playlistService.getPlaylistsByUser(userId);
        
        if (currentUser == null || (!ownershipChecker.isAdmin() && !currentUser.getId().equals(userId))) {
            playlists = playlists.stream()
                    .filter(Playlist::getIsPublic)
                    .toList();
        }
        
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        List<Playlist> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Playlist>> searchPlaylists(@RequestParam String name) {
        var currentUser = ownershipChecker.getCurrentUser();
        List<Playlist> playlists = playlistService.searchPlaylistsByName(name);
        
        if (currentUser == null) {
            playlists = playlists.stream()
                    .filter(Playlist::getIsPublic)
                    .toList();
        } else if (!ownershipChecker.isAdmin()) {
            playlists = playlists.stream()
                    .filter(p -> p.getIsPublic() || p.getUser().getId().equals(currentUser.getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(playlists);
    }
    
    @PostMapping("/{playlistId}/tracks")
    public ResponseEntity<PlaylistTrackResponse> addTrackToPlaylist(@PathVariable Long playlistId,
                                                            @RequestParam Long trackId,
                                                            @RequestParam(required = false) Integer position) {
        Playlist existing = playlistService.getPlaylistById(playlistId);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для изменения этого плейлиста");
        }
        PlaylistTrack playlistTrack = playlistService.addTrackToPlaylist(playlistId, trackId, position);
        Long trackIdentifier = playlistTrack.getTrack() != null ? playlistTrack.getTrack().getId() : null;
        List<PlaylistTrackResponse> view = playlistService.getPlaylistView(playlistId);
        PlaylistTrackResponse response = view.stream()
                .filter(item -> trackIdentifier != null && item.getTrackId().equals(trackIdentifier))
                .findFirst()
                .orElseGet(() -> {
                    if (!view.isEmpty()) {
                        return view.get(view.size() - 1);
                    }
                    return null;
                });
        if (response == null) {
            throw new RuntimeException("Не удалось получить информацию о добавленном треке");
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{playlistId}/tracks/move")
    public ResponseEntity<List<PlaylistTrackResponse>> moveTrackInPlaylist(@PathVariable Long playlistId,
                                                                           @Valid @RequestBody PlaylistMoveRequest request) {
        Playlist existing = playlistService.getPlaylistById(playlistId);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для изменения этого плейлиста");
        }
        playlistService.moveTrackWithinPlaylist(playlistId, request.getTrackId(), request.getNewPosition());
        return ResponseEntity.ok(playlistService.getPlaylistView(playlistId));
    }
    
    @PostMapping("/{playlistId}/shuffle")
    public ResponseEntity<List<PlaylistTrackResponse>> shufflePlaylist(@PathVariable Long playlistId) {
        Playlist existing = playlistService.getPlaylistById(playlistId);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для изменения этого плейлиста");
        }
        playlistService.shufflePlaylist(playlistId);
        return ResponseEntity.ok(playlistService.getPlaylistView(playlistId));
    }
    
    @PostMapping("/{playlistId}/clone")
    public ResponseEntity<Playlist> clonePlaylist(@PathVariable Long playlistId,
                                                  @Valid @RequestBody PlaylistCloneRequest request) {
        Playlist source = playlistService.getPlaylistById(playlistId);
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        boolean canClone = source.getIsPublic() || 
                          (source.getUser() != null && source.getUser().getId().equals(currentUser.getId())) || 
                          ownershipChecker.isAdmin();
        
        if (!canClone) {
            throw new AccessDeniedException("У вас нет прав для клонирования этого плейлиста");
        }
        
        Playlist clone = playlistService.clonePlaylist(playlistId, currentUser.getId(), request.getName(), request.getDescription(), request.getMakePublic());
        return new ResponseEntity<>(clone, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{playlistId}/tracks/{position}")
    public ResponseEntity<Void> removeTrackFromPlaylist(@PathVariable Long playlistId,
                                                        @PathVariable Integer position) {
        Playlist existing = playlistService.getPlaylistById(playlistId);
        if (!ownershipChecker.isOwnerOrAdmin(existing)) {
            throw new AccessDeniedException("У вас нет прав для изменения этого плейлиста");
        }
        playlistService.removeTrackFromPlaylist(playlistId, position);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<List<PlaylistTrackResponse>> getPlaylistTracks(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        var currentUser = ownershipChecker.getCurrentUser();
        
        if (!playlist.getIsPublic() && (currentUser == null || 
            (!ownershipChecker.isAdmin() && !playlist.getUser().getId().equals(currentUser.getId())))) {
            throw new AccessDeniedException("У вас нет доступа к этому плейлисту");
        }
        
        List<PlaylistTrackResponse> tracks = playlistService.getPlaylistView(playlistId);
        return ResponseEntity.ok(tracks);
    }
}
