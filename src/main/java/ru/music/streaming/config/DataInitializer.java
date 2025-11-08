package ru.music.streaming.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.*;
import ru.music.streaming.repository.*;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;

    public DataInitializer(ArtistRepository artistRepository,
                           AlbumRepository albumRepository,
                           TrackRepository trackRepository,
                           UserRepository userRepository,
                           PlaylistRepository playlistRepository,
                           PlaylistTrackRepository playlistTrackRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (artistRepository.count() > 0 || trackRepository.count() > 0) {
            return;
        }

        Artist macDemarco = artistRepository.save(new Artist("Mac DeMarco", "Canadian singer-songwriter", "Canada"));
        Artist tameImpala = artistRepository.save(new Artist("Tame Impala", "Australian psychedelic music project", "Australia"));

        Album saladDays = albumRepository.save(new Album("Salad Days", macDemarco, LocalDate.of(2014, 4, 1), "https://example.com/salad-days.jpg"));
        Album currents = albumRepository.save(new Album("Currents", tameImpala, LocalDate.of(2015, 7, 17), "https://example.com/currents.jpg"));

        Track chamber = new Track("Chamber Of Reflection", macDemarco, saladDays, 231, "Indie");
        chamber.setAudioUrl("https://example.com/audio/chamber.mp3");
        chamber = trackRepository.save(chamber);

        Track passingOut = new Track("Passing Out Pieces", macDemarco, saladDays, 207, "Indie");
        passingOut.setAudioUrl("https://example.com/audio/passing.mp3");
        passingOut = trackRepository.save(passingOut);

        Track letItHappen = new Track("Let It Happen", tameImpala, currents, 476, "Psychedelic");
        letItHappen.setAudioUrl("https://example.com/audio/letithappen.mp3");
        letItHappen = trackRepository.save(letItHappen);

        Track eventually = new Track("Eventually", tameImpala, currents, 321, "Psychedelic");
        eventually.setAudioUrl("https://example.com/audio/eventually.mp3");
        eventually = trackRepository.save(eventually);

        User alice = userRepository.save(new User("Alice", "Cooper", "alice@example.com"));
        User bob = userRepository.save(new User("Bob", "Stone", "bob@example.com"));

        Playlist chillVibes = playlistRepository.save(new Playlist("Chill Vibes", "Relaxed indie tracks", alice, true));
        Playlist psychEnergy = playlistRepository.save(new Playlist("Psychedelic Energy", "Favourite tracks from Tame Impala", bob, false));

        playlistTrackRepository.saveAll(List.of(
                new PlaylistTrack(chillVibes, chamber, 0),
                new PlaylistTrack(chillVibes, passingOut, 1),
                new PlaylistTrack(psychEnergy, letItHappen, 0),
                new PlaylistTrack(psychEnergy, eventually, 1)
        ));
    }
}
