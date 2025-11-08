package ru.music.streaming.dto;

public class PlaylistTrackResponse {

    private Long trackId;
    private String title;
    private String artist;
    private Integer position;
    private String album;
    private Integer durationSeconds;
    private String genre;

    public PlaylistTrackResponse(Long trackId, String title, String artist, Integer position,
                                 String album, Integer durationSeconds, String genre) {
        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.position = position;
        this.album = album;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
    }

    public Long getTrackId() {
        return trackId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Integer getPosition() {
        return position;
    }

    public String getAlbum() {
        return album;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public String getGenre() {
        return genre;
    }
}
