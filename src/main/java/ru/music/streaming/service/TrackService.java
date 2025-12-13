package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Album;
import ru.music.streaming.model.Artist;
import ru.music.streaming.model.Track;
import ru.music.streaming.repository.TrackRepository;

import java.util.List;

@Service
public class TrackService {
    
    private final TrackRepository trackRepository;
    private final ArtistService artistService;
    private final AlbumService albumService;
    
    @Autowired
    public TrackService(TrackRepository trackRepository, ArtistService artistService, AlbumService albumService) {
        this.trackRepository = trackRepository;
        this.artistService = artistService;
        this.albumService = albumService;
    }
    
    @Transactional
    public Track createTrack(Track track, Long artistId, Long albumId) {
        Artist artist = artistService.getArtistById(artistId);
        track.setArtist(artist);
        
        if (albumId != null) {
            Album album = albumService.getAlbumById(albumId);
            track.setAlbum(album);
        }
        
        return trackRepository.save(track);
    }
    
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }
    
    public Track getTrackById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Трек с ID " + id + " не найден"));
    }
    
    @Transactional
    public Track updateTrack(Long id, Track trackDetails, Long artistId, Long albumId) {
        Track track = getTrackById(id);
        
        track.setTitle(trackDetails.getTitle());
        track.setDurationSeconds(trackDetails.getDurationSeconds());
        track.setGenre(trackDetails.getGenre());
        track.setAudioUrl(trackDetails.getAudioUrl());
        
        if (artistId != null) {
            Artist artist = artistService.getArtistById(artistId);
            track.setArtist(artist);
        }
        
        if (albumId != null) {
            Album album = albumService.getAlbumById(albumId);
            track.setAlbum(album);
        }
        
        return trackRepository.save(track);
    }
    
    @Transactional
    public void deleteTrack(Long id) {
        Track track = getTrackById(id);
        trackRepository.delete(track);
    }
    
    public List<Track> getTracksByArtist(Long artistId) {
        return trackRepository.findByArtistId(artistId);
    }
    
    public List<Track> getTracksByAlbum(Long albumId) {
        return trackRepository.findByAlbumId(albumId);
    }
    
    public List<Track> searchTracksByTitle(String title) {
        return trackRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Track> getTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }
}
