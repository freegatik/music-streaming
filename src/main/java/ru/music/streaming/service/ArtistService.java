package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Artist;
import ru.music.streaming.repository.ArtistRepository;

import java.util.List;

@Service
public class ArtistService {
    
    private final ArtistRepository artistRepository;
    
    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }
    
    @Transactional
    public Artist createArtist(Artist artist) {
        return artistRepository.save(artist);
    }
    
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }
    
    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Артист с ID " + id + " не найден"));
    }
    
    @Transactional
    public Artist updateArtist(Long id, Artist artistDetails) {
        Artist artist = getArtistById(id);
        
        artist.setName(artistDetails.getName());
        artist.setBio(artistDetails.getBio());
        artist.setCountry(artistDetails.getCountry());
        
        return artistRepository.save(artist);
    }
    
    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = getArtistById(id);
        artistRepository.delete(artist);
    }
    
    public List<Artist> searchArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Artist> getArtistsByCountry(String country) {
        return artistRepository.findByCountry(country);
    }
}
