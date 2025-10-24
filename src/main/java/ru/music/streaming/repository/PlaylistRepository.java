package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.Playlist;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    List<Playlist> findByUserId(Long userId);
    
    List<Playlist> findByIsPublic(Boolean isPublic);
    
    List<Playlist> findByNameContainingIgnoreCase(String name);
}
