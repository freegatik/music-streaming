package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.Track;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    
    List<Track> findByArtistId(Long artistId);
    
    List<Track> findByAlbumId(Long albumId);
    
    List<Track> findByTitleContainingIgnoreCase(String title);
    
    List<Track> findByGenre(String genre);
}
