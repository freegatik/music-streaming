package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.model.Track;
import ru.music.streaming.service.TrackService;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {
    
    private final TrackService trackService;
    
    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }
    
    @PostMapping
    public ResponseEntity<Track> createTrack(@Valid @RequestBody Track track,
                                            @RequestParam Long artistId,
                                            @RequestParam(required = false) Long albumId) {
        Track created = trackService.createTrack(track, artistId, albumId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        Track track = trackService.getTrackById(id);
        return ResponseEntity.ok(track);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, 
                                            @Valid @RequestBody Track track) {
        Track updated = trackService.updateTrack(id, track);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Track>> getTracksByArtist(@PathVariable Long artistId) {
        List<Track> tracks = trackService.getTracksByArtist(artistId);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Track>> getTracksByAlbum(@PathVariable Long albumId) {
        List<Track> tracks = trackService.getTracksByAlbum(albumId);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam String title) {
        List<Track> tracks = trackService.searchTracksByTitle(title);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Track>> getTracksByGenre(@PathVariable String genre) {
        List<Track> tracks = trackService.getTracksByGenre(genre);
        return ResponseEntity.ok(tracks);
    }
}
