package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    
    private final PlaylistService playlistService;
    
    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
    
    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@Valid @RequestBody Playlist playlist,
                                                   @RequestParam Long userId) {
        Playlist created = playlistService.createPlaylist(playlist, userId);
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
        Playlist updated = playlistService.updatePlaylist(id, playlist);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
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
    public ResponseEntity<PlaylistTrack> addTrackToPlaylist(@PathVariable Long playlistId,
                                                            @RequestParam Long trackId,
                                                            @RequestParam(required = false) Integer position) {
        PlaylistTrack playlistTrack = playlistService.addTrackToPlaylist(playlistId, trackId, position);
        return new ResponseEntity<>(playlistTrack, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{playlistId}/tracks/{position}")
    public ResponseEntity<Void> removeTrackFromPlaylist(@PathVariable Long playlistId,
                                                        @PathVariable Integer position) {
        playlistService.removeTrackFromPlaylist(playlistId, position);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<List<PlaylistTrack>> getPlaylistTracks(@PathVariable Long playlistId) {
        List<PlaylistTrack> tracks = playlistService.getPlaylistTracks(playlistId);
        return ResponseEntity.ok(tracks);
    }
}
