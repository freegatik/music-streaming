package ru.music.streaming.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.music.streaming.model.Artist;
import ru.music.streaming.service.ArtistService;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    
    private final ArtistService artistService;
    
    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }
    
    @PostMapping
    public ResponseEntity<Artist> createArtist(@Valid @RequestBody Artist artist) {
        Artist created = artistService.createArtist(artist);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        Artist artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable Long id, 
                                               @Valid @RequestBody Artist artist) {
        Artist updated = artistService.updateArtist(id, artist);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String name) {
        List<Artist> artists = artistService.searchArtistsByName(name);
        return ResponseEntity.ok(artists);
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Artist>> getArtistsByCountry(@PathVariable String country) {
        List<Artist> artists = artistService.getArtistsByCountry(country);
        return ResponseEntity.ok(artists);
    }
}
