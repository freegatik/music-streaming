package ru.music.streaming.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Имя артиста не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя артиста должно быть от 1 до 100 символов")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String bio;
    
    @Column(name = "country")
    private String country;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Track> tracks = new ArrayList<>();
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Album> albums = new ArrayList<>();
    
    public Artist() {
    }
    
    public Artist(String name, String bio, String country) {
        this.name = name;
        this.bio = bio;
        this.country = country;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public List<Track> getTracks() {
        return tracks;
    }
    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
    
    public List<Album> getAlbums() {
        return albums;
    }
    
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
