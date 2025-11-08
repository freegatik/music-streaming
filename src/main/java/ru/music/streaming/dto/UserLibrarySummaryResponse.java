package ru.music.streaming.dto;

public class UserLibrarySummaryResponse {

    private final Long userId;
    private final int playlistsCount;
    private final int totalTracks;
    private final int uniqueTracks;
    private final int uniqueArtists;
    private final int totalDurationSeconds;

    public UserLibrarySummaryResponse(Long userId, int playlistsCount, int totalTracks, int uniqueTracks,
                                      int uniqueArtists, int totalDurationSeconds) {
        this.userId = userId;
        this.playlistsCount = playlistsCount;
        this.totalTracks = totalTracks;
        this.uniqueTracks = uniqueTracks;
        this.uniqueArtists = uniqueArtists;
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public Long getUserId() {
        return userId;
    }

    public int getPlaylistsCount() {
        return playlistsCount;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public int getUniqueTracks() {
        return uniqueTracks;
    }

    public int getUniqueArtists() {
        return uniqueArtists;
    }

    public int getTotalDurationSeconds() {
        return totalDurationSeconds;
    }
}
