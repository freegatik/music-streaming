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

    List<PlaylistTrack> findByPlaylistId(Long playlistId);

    Optional<PlaylistTrack> findByPlaylistIdAndPosition(Long playlistId, Integer position);

    Optional<PlaylistTrack> findByPlaylistIdAndTrackId(Long playlistId, Long trackId);

    boolean existsByPlaylistIdAndPosition(Long playlistId, Integer position);

    boolean existsByPlaylistIdAndTrackId(Long playlistId, Long trackId);

    @Query("SELECT MAX(pt.position) FROM PlaylistTrack pt WHERE pt.playlist.id = :playlistId")
    Integer findMaxPositionByPlaylistId(Long playlistId);

    @Query("SELECT pt FROM PlaylistTrack pt WHERE pt.playlist.user.id = :userId")
    List<PlaylistTrack> findByUserId(Long userId);
}
