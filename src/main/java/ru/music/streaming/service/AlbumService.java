package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Album;
import ru.music.streaming.model.Artist;
import ru.music.streaming.repository.AlbumRepository;

import java.util.List;

@Service
public class AlbumService {
    
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    
    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistService artistService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
    }
    
    @Transactional
    public Album createAlbum(Album album, Long artistId) {
        Artist artist = artistService.getArtistById(artistId);
        album.setArtist(artist);
        return albumRepository.save(album);
    }
    
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }
    
    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Альбом с ID " + id + " не найден"));
    }
    
    @Transactional
    public Album updateAlbum(Long id, Album albumDetails, Long artistId) {
        Album album = getAlbumById(id);
        
        album.setTitle(albumDetails.getTitle());
        album.setReleaseDate(albumDetails.getReleaseDate());
        album.setCoverUrl(albumDetails.getCoverUrl());
        
        if (artistId != null) {
            Artist artist = artistService.getArtistById(artistId);
            album.setArtist(artist);
        }
        
        return albumRepository.save(album);
    }
    
    @Transactional
    public void deleteAlbum(Long id) {
        Album album = getAlbumById(id);
        albumRepository.delete(album);
    }
    
    public List<Album> getAlbumsByArtist(Long artistId) {
        return albumRepository.findByArtistId(artistId);
    }
    
    public List<Album> searchAlbumsByTitle(String title) {
        return albumRepository.findByTitleContainingIgnoreCase(title);
    }
}
