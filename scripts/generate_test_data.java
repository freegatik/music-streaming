package ru.music.streaming.scripts;

import java.util.*;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {
    
    private static final Random random = new Random();
    
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
    
    public static String generateArtistName() {
        return ARTIST_NAMES[random.nextInt(ARTIST_NAMES.length)];
    }
    
    public static String generateCountry() {
        return COUNTRIES[random.nextInt(COUNTRIES.length)];
    }
    
    public static String generateBio(String genre, String country) {
        String template = BIO_TEMPLATES[random.nextInt(BIO_TEMPLATES.length)];
        return template.replace("{genre}", genre).replace("{country}", country);
    }
    
    public static String generateAlbumTitle() {
        return ALBUM_TITLES[random.nextInt(ALBUM_TITLES.length)];
    }
    
    public static LocalDate generateReleaseDate() {
        int year = 1990 + random.nextInt(35);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return LocalDate.of(year, month, day);
    }
    
    public static String generateCoverUrl() {
        return "https://example.com/covers/" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
    }
    
    public static String generateTrackTitle() {
        return TRACK_TITLES[random.nextInt(TRACK_TITLES.length)];
    }
    
    public static int generateDuration() {
        return 120 + random.nextInt(300);
    }
    
    public static String generateGenre() {
        return GENRES[random.nextInt(GENRES.length)];
    }
    
    public static String generateAudioUrl() {
        return "https://example.com/audio/" + UUID.randomUUID().toString().substring(0, 8) + ".mp3";
    }
    
    public static String generatePlaylistName() {
        return PLAYLIST_NAMES[random.nextInt(PLAYLIST_NAMES.length)];
    }
    
    public static String generatePlaylistDescription() {
        return PLAYLIST_DESCRIPTIONS[random.nextInt(PLAYLIST_DESCRIPTIONS.length)];
    }
    
    public static boolean generateIsPublic() {
        return random.nextDouble() < 0.6;
    }
    
    public static class ArtistData {
        public final String name;
        public final String bio;
        public final String country;
        
        public ArtistData(String name, String bio, String country) {
            this.name = name;
            this.bio = bio;
            this.country = country;
        }
    }
    
    public static class AlbumData {
        public final String title;
        public final LocalDate releaseDate;
        public final String coverUrl;
        
        public AlbumData(String title, LocalDate releaseDate, String coverUrl) {
            this.title = title;
            this.releaseDate = releaseDate;
            this.coverUrl = coverUrl;
        }
    }
    
    public static class TrackData {
        public final String title;
        public final int durationSeconds;
        public final String genre;
        public final String audioUrl;
        
        public TrackData(String title, int durationSeconds, String genre, String audioUrl) {
            this.title = title;
            this.durationSeconds = durationSeconds;
            this.genre = genre;
            this.audioUrl = audioUrl;
        }
    }
    
    public static class PlaylistData {
        public final String name;
        public final String description;
        public final boolean isPublic;
        
        public PlaylistData(String name, String description, boolean isPublic) {
            this.name = name;
            this.description = description;
            this.isPublic = isPublic;
        }
    }
    
    public static List<ArtistData> generateArtists(int count) {
        List<ArtistData> artists = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String name;
            do {
                name = generateArtistName();
            } while (usedNames.contains(name) && usedNames.size() < ARTIST_NAMES.length);
            
            usedNames.add(name);
            String country = generateCountry();
            String genre = generateGenre();
            String bio = generateBio(genre, country);
            
            artists.add(new ArtistData(name, bio, country));
        }
        
        return artists;
    }
    
    public static List<AlbumData> generateAlbums(int count) {
        List<AlbumData> albums = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            albums.add(new AlbumData(
                generateAlbumTitle(),
                generateReleaseDate(),
                generateCoverUrl()
            ));
        }
        
        return albums;
    }
    
    public static List<TrackData> generateTracks(int count) {
        List<TrackData> tracks = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            tracks.add(new TrackData(
                generateTrackTitle(),
                generateDuration(),
                generateGenre(),
                generateAudioUrl()
            ));
        }
        
        return tracks;
    }
    
    public static List<PlaylistData> generatePlaylists(int count) {
        List<PlaylistData> playlists = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            playlists.add(new PlaylistData(
                generatePlaylistName(),
                generatePlaylistDescription(),
                generateIsPublic()
            ));
        }
        
        return playlists;
    }
}

