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
        // Используем текущего аутентифицированного пользователя
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        Playlist created = playlistService.createPlaylist(playlist, currentUser.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
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
        List<Playlist> playlists = playlistService.getPlaylistsByUser(userId);
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        List<Playlist> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Playlist>> searchPlaylists(@RequestParam String name) {
        List<Playlist> playlists = playlistService.searchPlaylistsByName(name);
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
        Long trackIdentifier = playlistTrack.getTrack().getId();
        List<PlaylistTrackResponse> view = playlistService.getPlaylistView(playlistId);
        PlaylistTrackResponse response = view.stream()
                .filter(item -> item.getTrackId().equals(trackIdentifier))
                .findFirst()
                .orElseGet(() -> view.get(view.size() - 1));
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
        // Проверяем доступ к исходному плейлисту (должен быть публичным или принадлежать пользователю)
        Playlist source = playlistService.getPlaylistById(playlistId);
        var currentUser = ownershipChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        
        // Плейлист может быть клонирован, если он публичный или принадлежит пользователю, или пользователь - ADMIN
        boolean canClone = source.getIsPublic() || 
                          source.getUser().getId().equals(currentUser.getId()) || 
                          ownershipChecker.isAdmin();
        
        if (!canClone) {
            throw new AccessDeniedException("У вас нет прав для клонирования этого плейлиста");
        }
        
        // Клонируем для текущего пользователя
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
        List<PlaylistTrackResponse> tracks = playlistService.getPlaylistView(playlistId);
        return ResponseEntity.ok(tracks);
    }
}
