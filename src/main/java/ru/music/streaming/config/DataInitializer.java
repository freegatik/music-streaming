package ru.music.streaming.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Album;
import ru.music.streaming.model.Artist;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.Role;
import ru.music.streaming.model.Track;
import ru.music.streaming.model.User;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.repository.AlbumRepository;
import ru.music.streaming.repository.ArtistRepository;
import ru.music.streaming.repository.PlaylistRepository;
import ru.music.streaming.repository.PlaylistTrackRepository;
import ru.music.streaming.repository.TrackRepository;
import ru.music.streaming.repository.UserRepository;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ArtistRepository artistRepository,
                           AlbumRepository albumRepository,
                           TrackRepository trackRepository,
                           UserRepository userRepository,
                           PlaylistRepository playlistRepository,
                           PlaylistTrackRepository playlistTrackRepository,
                           PasswordEncoder passwordEncoder) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.findByUsername("admin").ifPresentOrElse(
                admin -> {
                    admin.setPassword(passwordEncoder.encode("Admin123!@#"));
                    admin.setRole(Role.ADMIN);
                    admin.setEmail("admin@example.com");
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    userRepository.save(admin);
                },
                () -> {
                    User admin = new User(
                            "Admin",
                            "User",
                            "admin@example.com",
                            "admin",
                            passwordEncoder.encode("Admin123!@#"),
                            Role.ADMIN
                    );
                    userRepository.save(admin);
                }
        );

        userRepository.findByUsername("testuser").ifPresentOrElse(
                testUser -> {
                    testUser.setPassword(passwordEncoder.encode("Test123!@#"));
                    testUser.setRole(Role.USER);
                    testUser.setEmail("user@example.com");
                    testUser.setFirstName("Test");
                    testUser.setLastName("User");
                    userRepository.save(testUser);
                },
                () -> {
                    User testUser = new User(
                            "Test",
                            "User",
                            "user@example.com",
                            "testuser",
                            passwordEncoder.encode("Test123!@#"),
                            Role.USER
                    );
                    userRepository.save(testUser);
                }
        );
        
        Track chamber = null;
        Track passingOut = null;
        Track letItHappen = null;
        Track eventually = null;

        if (artistRepository.count() == 0 && trackRepository.count() == 0) {
            Artist macDemarco = artistRepository.save(new Artist("Mac DeMarco", "Canadian singer-songwriter", "Canada"));
            Artist tameImpala = artistRepository.save(new Artist("Tame Impala", "Australian psychedelic music project", "Australia"));

            Album saladDays = albumRepository.save(new Album("Salad Days", macDemarco, LocalDate.of(2014, 4, 1), "https://example.com/salad-days.jpg"));
            Album currents = albumRepository.save(new Album("Currents", tameImpala, LocalDate.of(2015, 7, 17), "https://example.com/currents.jpg"));

            chamber = new Track("Chamber Of Reflection", macDemarco, saladDays, 231, "Indie");
            chamber.setAudioUrl("https://example.com/audio/chamber.mp3");
            chamber = trackRepository.save(chamber);

            passingOut = new Track("Passing Out Pieces", macDemarco, saladDays, 207, "Indie");
            passingOut.setAudioUrl("https://example.com/audio/passing.mp3");
            passingOut = trackRepository.save(passingOut);

            letItHappen = new Track("Let It Happen", tameImpala, currents, 476, "Psychedelic");
            letItHappen.setAudioUrl("https://example.com/audio/letithappen.mp3");
            letItHappen = trackRepository.save(letItHappen);

            eventually = new Track("Eventually", tameImpala, currents, 321, "Psychedelic");
            eventually.setAudioUrl("https://example.com/audio/eventually.mp3");
            eventually = trackRepository.save(eventually);
        } else {
            var allTracks = trackRepository.findAll();
            for (Track track : allTracks) {
                if (chamber == null && track.getTitle().equals("Chamber Of Reflection")) {
                    chamber = track;
                } else if (passingOut == null && track.getTitle().equals("Passing Out Pieces")) {
                    passingOut = track;
                } else if (letItHappen == null && track.getTitle().equals("Let It Happen")) {
                    letItHappen = track;
                } else if (eventually == null && track.getTitle().equals("Eventually")) {
                    eventually = track;
                }
            }
            
            if (chamber == null || passingOut == null || letItHappen == null || eventually == null) {
                var anyTracks = trackRepository.findAll();
                if (!anyTracks.isEmpty()) {
                    if (chamber == null) chamber = anyTracks.get(0);
                    if (passingOut == null && anyTracks.size() > 1) passingOut = anyTracks.get(1);
                    if (letItHappen == null && anyTracks.size() > 2) letItHappen = anyTracks.get(2);
                    if (eventually == null && anyTracks.size() > 3) eventually = anyTracks.get(3);
                    else if (eventually == null && anyTracks.size() > 0) eventually = anyTracks.get(0);
                }
            }
        }

        User admin = userRepository.findByUsername("admin").orElseThrow();
        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        
        if (chamber != null && passingOut != null && letItHappen != null && eventually != null) {
            if (playlistRepository.count() == 0) {
                Playlist publicPlaylist1 = new Playlist("Chill Vibes", "Relaxing indie tracks", admin, true);
                publicPlaylist1 = playlistRepository.save(publicPlaylist1);
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist1, chamber, 0));
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist1, passingOut, 1));
                
                Playlist publicPlaylist2 = new Playlist("Indie Mix", "Best indie tracks collection", testUser, true);
                publicPlaylist2 = playlistRepository.save(publicPlaylist2);
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist2, chamber, 0));
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist2, letItHappen, 1));
                
                Playlist publicPlaylist3 = new Playlist("Workout Mix", "Get pumped up", admin, true);
                publicPlaylist3 = playlistRepository.save(publicPlaylist3);
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist3, letItHappen, 0));
                playlistTrackRepository.save(new PlaylistTrack(publicPlaylist3, eventually, 1));
            }
            
            var existingPlaylists = playlistRepository.findAll();
            for (Playlist playlist : existingPlaylists) {
                var existingTracks = playlistTrackRepository.findByPlaylistId(playlist.getId());
                if (existingTracks.isEmpty()) {
                    if (playlist.getName().equals("Chill Vibes")) {
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), chamber.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, chamber, 0));
                        }
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), passingOut.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, passingOut, 1));
                        }
                    } else if (playlist.getName().equals("Indie Mix")) {
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), chamber.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, chamber, 0));
                        }
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), letItHappen.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, letItHappen, 1));
                        }
                    } else if (playlist.getName().equals("Workout Mix")) {
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), letItHappen.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, letItHappen, 0));
                        }
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), eventually.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, eventually, 1));
                        }
                    } else if (chamber != null) {
                        if (!playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), chamber.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, chamber, 0));
                        }
                        if (passingOut != null && !playlistTrackRepository.existsByPlaylistIdAndTrackId(playlist.getId(), passingOut.getId())) {
                            playlistTrackRepository.save(new PlaylistTrack(playlist, passingOut, 1));
                        }
                    }
                }
            }
        }
    }
}
