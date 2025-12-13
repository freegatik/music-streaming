package ru.music.streaming.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Album;
import ru.music.streaming.model.Artist;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.model.Track;
import ru.music.streaming.model.User;
import ru.music.streaming.repository.AlbumRepository;
import ru.music.streaming.repository.ArtistRepository;
import ru.music.streaming.repository.PlaylistRepository;
import ru.music.streaming.repository.PlaylistTrackRepository;
import ru.music.streaming.repository.TrackRepository;
import ru.music.streaming.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final Random random = new Random();

    private static final String[] ARTIST_NAMES = {
        "Arctic Monkeys", "The Beatles", "Radiohead", "Pink Floyd", "Led Zeppelin",
        "The Rolling Stones", "Queen", "Nirvana", "The Doors", "David Bowie",
        "The Cure", "Joy Division", "New Order", "Depeche Mode", "The Smiths",
        "Tame Impala", "Mac DeMarco", "King Gizzard", "Unknown Mortal Orchestra", "Beach House",
        "Daft Punk", "Justice", "MGMT", "Phoenix", "LCD Soundsystem",
        "Arcade Fire", "Interpol", "The Strokes", "The White Stripes", "The Black Keys"
    };

    private static final String[] COUNTRIES = {
        "UK", "USA", "Canada", "Australia", "France", "Germany", "Sweden", "Iceland",
        "Ireland", "New Zealand", "Japan", "Brazil", "Argentina", "Spain", "Italy"
    };

    private static final String[] ALBUM_TITLES = {
        "Midnight Dreams", "Electric Nights", "Ocean Waves", "Mountain Peaks", "City Lights",
        "Desert Storm", "Forest Echoes", "River Flow", "Sky High", "Deep Blue",
        "Golden Hour", "Silver Moon", "Crystal Clear", "Dark Matter", "Bright Future",
        "Lost Paradise", "Hidden Truth", "Silent Storm", "Wild Fire", "Cold Wind",
        "Hot Summer", "Autumn Leaves", "Winter Snow", "Spring Rain", "Summer Breeze"
    };

    private static final String[] TRACK_TITLES = {
        "Midnight", "Sunrise", "Sunset", "Dawn", "Dusk",
        "Echo", "Silence", "Whisper", "Scream", "Shout",
        "Dream", "Reality", "Fantasy", "Illusion", "Vision",
        "Storm", "Calm", "Chaos", "Order", "Balance",
        "Fire", "Water", "Earth", "Air", "Light",
        "Shadow", "Darkness", "Brightness", "Glimmer", "Sparkle"
    };

    private static final String[] GENRES = {
        "Rock", "Pop", "Indie", "Electronic", "Jazz", "Blues", "Folk", "Country",
        "Hip-Hop", "R&B", "Reggae", "Classical", "Metal", "Punk", "Alternative",
        "Psychedelic", "Ambient", "Techno", "House", "Drum & Bass"
    };

    private static final String[] PLAYLIST_NAMES = {
        "Chill Vibes", "Workout Mix", "Study Session", "Road Trip", "Party Time",
        "Relaxation", "Energy Boost", "Focus Mode", "Sleep Well", "Morning Coffee",
        "Evening Wind Down", "Weekend Vibes", "Summer Hits", "Winter Warmth",
        "Rainy Day", "Sunny Day", "Night Drive", "Beach Party", "Mountain View", "City Walk"
    };

    private static final String[] PLAYLIST_DESCRIPTIONS = {
        "Perfect for relaxing", "Get pumped up", "Focus and concentration", "Long journey companion",
        "Dance the night away", "Unwind after work", "Boost your energy", "Deep focus playlist",
        "Peaceful sleep", "Start your day right", "End your day calm", "Weekend mood",
        "Summer favorites", "Cozy winter sounds", "Rainy day comfort", "Sunshine vibes",
        "Nighttime cruising", "Beach atmosphere", "Nature sounds", "Urban exploration"
    };

    private static final String[] BIO_TEMPLATES = {
        "Award-winning {genre} artist from {country}",
        "Emerging {genre} talent making waves in the music scene",
        "Veteran {genre} performer with decades of experience",
        "Innovative {genre} musician pushing boundaries",
        "Critically acclaimed {genre} artist from {country}",
        "Rising star in the {genre} world",
        "Legendary {genre} icon from {country}",
        "Experimental {genre} artist exploring new sounds"
    };

    public TestDataGenerator(ArtistRepository artistRepository,
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
        if (args.length > 0 && "generate".equals(args[0])) {
            generateTestData();
            System.exit(0);
        }
    }

    @Transactional
    public void generateTestData() {
        int artistsCount = 20;
        int albumsPerArtist = 2;
        int tracksPerAlbum = 5;
        int playlistsCount = 15;

        User admin = userRepository.findByUsername("admin").orElseThrow();
        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        List<User> users = Arrays.asList(admin, testUser);

        List<Artist> artists = generateArtists(artistsCount);
        List<Album> albums = new ArrayList<>();
        List<Track> tracks = new ArrayList<>();

        for (Artist artist : artists) {
            for (int i = 0; i < albumsPerArtist; i++) {
                Album album = generateAlbum(artist);
                albums.add(album);
                albumRepository.save(album);

                for (int j = 0; j < tracksPerAlbum; j++) {
                    Track track = generateTrack(artist, album);
                    tracks.add(track);
                    trackRepository.save(track);
                }
            }
        }

        List<Playlist> playlists = new ArrayList<>();
        for (int i = 0; i < playlistsCount; i++) {
            User owner = users.get(random.nextInt(users.size()));
            Playlist playlist = generatePlaylist(owner);
            playlist = playlistRepository.save(playlist);
            playlists.add(playlist);
        }

        for (Playlist playlist : playlists) {
            int tracksInPlaylist = 5 + random.nextInt(15);
            List<Track> shuffledTracks = new ArrayList<>(tracks);
            Collections.shuffle(shuffledTracks);
            Set<Long> usedTrackIds = new HashSet<>();
            int position = 0;
            for (Track track : shuffledTracks) {
                if (usedTrackIds.contains(track.getId())) {
                    continue;
                }
                if (position >= tracksInPlaylist) {
                    break;
                }
                playlistTrackRepository.save(new PlaylistTrack(playlist, track, position++));
                usedTrackIds.add(track.getId());
            }
        }

        System.out.println("Generated: " + artistsCount + " artists, " + albums.size() + " albums, " + tracks.size() + " tracks, " + playlistsCount + " playlists with tracks");
    }

    private List<Artist> generateArtists(int count) {
        List<Artist> artists = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String name;
            do {
                name = ARTIST_NAMES[random.nextInt(ARTIST_NAMES.length)];
            } while (usedNames.contains(name) && usedNames.size() < ARTIST_NAMES.length);

            usedNames.add(name);
            String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
            String genre = GENRES[random.nextInt(GENRES.length)];
            String bio = BIO_TEMPLATES[random.nextInt(BIO_TEMPLATES.length)]
                    .replace("{genre}", genre)
                    .replace("{country}", country);

            Artist artist = new Artist(name, bio, country);
            artists.add(artistRepository.save(artist));
        }

        return artists;
    }

    private Album generateAlbum(Artist artist) {
        String title = ALBUM_TITLES[random.nextInt(ALBUM_TITLES.length)];
        int year = 1990 + random.nextInt(35);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        LocalDate releaseDate = LocalDate.of(year, month, day);
        String coverUrl = "https://example.com/covers/" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";

        return new Album(title, artist, releaseDate, coverUrl);
    }

    private Track generateTrack(Artist artist, Album album) {
        String title = TRACK_TITLES[random.nextInt(TRACK_TITLES.length)];
        int duration = 120 + random.nextInt(300);
        String genre = GENRES[random.nextInt(GENRES.length)];
        String audioUrl = "https://example.com/audio/" + UUID.randomUUID().toString().substring(0, 8) + ".mp3";

        Track track = new Track(title, artist, album, duration, genre);
        track.setAudioUrl(audioUrl);
        return track;
    }

    private Playlist generatePlaylist(User user) {
        String name = PLAYLIST_NAMES[random.nextInt(PLAYLIST_NAMES.length)];
        String description = PLAYLIST_DESCRIPTIONS[random.nextInt(PLAYLIST_DESCRIPTIONS.length)];
        boolean isPublic = random.nextDouble() < 0.6;

        return new Playlist(name, description, user, isPublic);
    }
}

