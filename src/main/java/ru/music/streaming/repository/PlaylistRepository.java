package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.Playlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.playlistTracks WHERE p.id = :id")
    Optional<Playlist> findByIdWithTracks(Long id);
    
    List<Playlist> findByUserId(Long userId);
    
    List<Playlist> findByIsPublic(Boolean isPublic);
    
    List<Playlist> findByNameContainingIgnoreCase(String name);
}
