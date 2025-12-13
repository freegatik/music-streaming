package ru.music.streaming.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "playlist_tracks", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"playlist_id", "position"}))
public class PlaylistTrack {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Укажите плейлист")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    @JsonIgnore
    private Playlist playlist;
    
    @NotNull(message = "Укажите трек")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    @JsonIgnore
    private Track track;
    
    @Column(nullable = false)
    private Integer position;
    
    public PlaylistTrack() {
    }
    
    public PlaylistTrack(Playlist playlist, Track track, Integer position) {
        this.playlist = playlist;
        this.track = track;
        this.position = position;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Playlist getPlaylist() {
        return playlist;
    }
    
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
    
    public Track getTrack() {
        return track;
    }
    
    public void setTrack(Track track) {
        this.track = track;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
}
