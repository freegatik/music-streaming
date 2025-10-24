package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.Artist;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    List<Artist> findByNameContainingIgnoreCase(String name);
    
    List<Artist> findByCountry(String country);
}
