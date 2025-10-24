package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.PlaylistTrack;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {
    
    List<PlaylistTrack> findByPlaylistIdOrderByPositionAsc(Long playlistId);
    
    Optional<PlaylistTrack> findByPlaylistIdAndPosition(Long playlistId, Integer position);
    
    boolean existsByPlaylistIdAndPosition(Long playlistId, Integer position);
    
    @Query("SELECT MAX(pt.position) FROM PlaylistTrack pt WHERE pt.playlist.id = :playlistId")
    Integer findMaxPositionByPlaylistId(Long playlistId);
}
