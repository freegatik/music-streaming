package ru.music.streaming.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Album;
import ru.music.streaming.model.Artist;
import ru.music.streaming.model.Track;
import ru.music.streaming.repository.AlbumRepository;
import ru.music.streaming.repository.ArtistRepository;
import ru.music.streaming.repository.TrackRepository;
import ru.music.streaming.repository.UserRepository;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public DataInitializer(ArtistRepository artistRepository,
                           AlbumRepository albumRepository,
                           TrackRepository trackRepository,
                           UserRepository userRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.deleteAll();
        
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
    }
}
