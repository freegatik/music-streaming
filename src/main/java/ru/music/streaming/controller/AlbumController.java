package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.model.Album;
import ru.music.streaming.service.AlbumService;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    
    private final AlbumService albumService;
    
    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }
    
    @PostMapping
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody Album album, 
                                            @RequestParam Long artistId) {
        Album created = albumService.createAlbum(album, artistId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        List<Album> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        Album album = albumService.getAlbumById(id);
        return ResponseEntity.ok(album);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, 
                                            @Valid @RequestBody Album album,
                                            @RequestParam(required = false) Long artistId) {
        Album updated = albumService.updateAlbum(id, album, artistId);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable Long artistId) {
        List<Album> albums = albumService.getAlbumsByArtist(artistId);
        return ResponseEntity.ok(albums);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Album>> searchAlbums(@RequestParam String title) {
        List<Album> albums = albumService.searchAlbumsByTitle(title);
        return ResponseEntity.ok(albums);
    }
}
