package ru.music.streaming.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название альбома не может быть пустым")
    @Size(min = 1, max = 200, message = "Название альбома должно быть от 1 до 200 символов")
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @JsonIgnore
    private Artist artist;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "cover_url")
    private String coverUrl;
    
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Track> tracks = new ArrayList<>();
    
    public Album() {
    }
    
    public Album(String title, Artist artist, LocalDate releaseDate, String coverUrl) {
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
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
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getCoverUrl() {
        return coverUrl;
    }
    
    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
    
    public List<Track> getTracks() {
        return tracks;
    }
    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
