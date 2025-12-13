package ru.music.streaming.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tracks")
public class Track {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название трека не может быть пустым")
    @Size(min = 1, max = 200, message = "Название трека должно быть от 1 до 200 символов")
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @JsonIgnore
    private Artist artist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonIgnore
    private Album album;
    
    @Min(value = 1, message = "Длительность должна быть больше 0")
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;
    
    @Column(name = "genre")
    private String genre;
    
    @Column(name = "audio_url")
    private String audioUrl;
    
    public Track() {
    }
    
    public Track(String title, Artist artist, Album album, Integer durationSeconds, String genre) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Artist getArtist() {
        return artist;
    }
    
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    
    public Album getAlbum() {
        return album;
    }
    
    public void setAlbum(Album album) {
        this.album = album;
    }
    
    public Integer getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getAudioUrl() {
        return audioUrl;
    }
    
    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
