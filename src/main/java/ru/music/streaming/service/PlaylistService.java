package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.dto.DailyMixRequest;
import ru.music.streaming.dto.PlaylistTrackResponse;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.model.Track;
import ru.music.streaming.model.User;
import ru.music.streaming.repository.PlaylistRepository;
import ru.music.streaming.repository.PlaylistTrackRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final UserService userService;
    private final TrackService trackService;
    
    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, 
                          PlaylistTrackRepository playlistTrackRepository,
                          UserService userService, 
                          TrackService trackService) {
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
        this.userService = userService;
        this.trackService = trackService;
    }
    
    @Transactional
    public Playlist createPlaylist(Playlist playlist, Long userId) {
        User user = userService.getUserById(userId);
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }
    
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }
    
    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Плейлист с ID " + id + " не найден"));
    }
    
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlistDetails) {
        Playlist playlist = getPlaylistById(id);
        
        playlist.setName(playlistDetails.getName());
        playlist.setDescription(playlistDetails.getDescription());
        playlist.setIsPublic(playlistDetails.getIsPublic());
        
        return playlistRepository.save(playlist);
    }
    
    @Transactional
    public void deletePlaylist(Long id) {
        Playlist playlist = getPlaylistById(id);
        playlistRepository.delete(playlist);
    }
    
    public List<Playlist> getPlaylistsByUser(Long userId) {
        return playlistRepository.findByUserId(userId);
    }
    
    public List<Playlist> getPublicPlaylists() {
        return playlistRepository.findByIsPublic(true);
    }
    
    public List<Playlist> searchPlaylistsByName(String name) {
        return playlistRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Transactional
    public PlaylistTrack addTrackToPlaylist(Long playlistId, Long trackId, Integer position) {
        Playlist playlist = getPlaylistById(playlistId);
        Track track = trackService.getTrackById(trackId);
        
        if (playlistTrackRepository.existsByPlaylistIdAndTrackId(playlistId, trackId)) {
            throw new RuntimeException("Трек уже существует в плейлисте");
        }
        
        if (position == null) {
            Integer maxPosition = playlistTrackRepository.findMaxPositionByPlaylistId(playlistId);
            position = (maxPosition == null) ? 0 : maxPosition + 1;
        }
        
        if (playlistTrackRepository.existsByPlaylistIdAndPosition(playlistId, position)) {
            throw new RuntimeException("Позиция " + position + " в плейлисте уже занята");
        }
        
        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track, position);
        return playlistTrackRepository.save(playlistTrack);
    }
    
    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, Integer position) {
        PlaylistTrack playlistTrack = playlistTrackRepository
                .findByPlaylistIdAndPosition(playlistId, position)
                .orElseThrow(() -> new RuntimeException("Трек на позиции " + position + " не найден"));
        
        playlistTrackRepository.delete(playlistTrack);
        normalizePositions(playlistId);
    }
    
    public List<PlaylistTrack> getPlaylistTracks(Long playlistId) {
        return playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
    }
    
    @Transactional
    public void moveTrackWithinPlaylist(Long playlistId, Long trackId, Integer newPosition) {
        if (newPosition == null) {
            throw new RuntimeException("Укажите новую позицию трека");
        }
        Playlist playlist = getPlaylistById(playlistId);
        List<PlaylistTrack> tracks = new ArrayList<>(playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlist.getId()));
        if (tracks.isEmpty()) {
            throw new RuntimeException("Плейлист пуст");
        }
        PlaylistTrack target = tracks.stream()
                .filter(pt -> Objects.equals(pt.getTrack().getId(), trackId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Трек не найден в плейлисте"));
        tracks.remove(target);
        int boundedPosition = Math.max(0, Math.min(newPosition, tracks.size()));
        tracks.add(boundedPosition, target);
        persistPositions(tracks);
    }
    
    @Transactional
    public void shufflePlaylist(Long playlistId) {
        Playlist playlist = getPlaylistById(playlistId);
        List<PlaylistTrack> tracks = new ArrayList<>(playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlist.getId()));
        if (tracks.isEmpty()) {
            throw new RuntimeException("Плейлист пуст");
        }
        Collections.shuffle(tracks);
        persistPositions(tracks);
    }
    
    @Transactional
    public Playlist clonePlaylist(Long sourcePlaylistId, Long targetUserId, String name, String description, Boolean makePublic) {
        Playlist source = getPlaylistById(sourcePlaylistId);
        User targetUser = userService.getUserById(targetUserId);
        String cloneName = (name != null && !name.isBlank())
                ? name.trim()
                : source.getName() + " (копия)";
        Playlist clone = new Playlist(cloneName, description != null ? description : source.getDescription(),
                targetUser, Boolean.TRUE.equals(makePublic));
        clone = playlistRepository.save(clone);
        List<PlaylistTrack> sourceTracks = playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(sourcePlaylistId);
        int index = 0;
        for (PlaylistTrack track : sourceTracks) {
            playlistTrackRepository.save(new PlaylistTrack(clone, track.getTrack(), index++));
        }
        return clone;
    }
    
    @Transactional
    public Playlist createDailyMix(Long userId, DailyMixRequest request) {
        User user = userService.getUserById(userId);
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        String genre = request.getGenre() != null ? request.getGenre().trim() : null;
        String name = (request.getName() != null && !request.getName().isBlank())
                ? request.getName().trim()
                : "Daily Mix " + LocalDate.now();
        String description = (request.getDescription() != null && !request.getDescription().isBlank())
                ? request.getDescription().trim()
                : "Автоматический плейлист на основе ваших любимых треков";
        
        List<PlaylistTrack> userTracks = playlistTrackRepository.findByUserId(userId);
        userTracks.sort(Comparator
                .comparing((PlaylistTrack pt) -> pt.getPlaylist().getCreatedAt())
                .thenComparingInt(PlaylistTrack::getPosition));
        Set<Track> candidateTracks = new LinkedHashSet<>();
        for (PlaylistTrack pt : userTracks) {
            Track track = pt.getTrack();
            if (genre == null || (track.getGenre() != null && track.getGenre().equalsIgnoreCase(genre))) {
                candidateTracks.add(track);
            }
        }
        if (candidateTracks.size() < limit) {
            List<Track> fallback = genre != null ? trackService.getTracksByGenre(genre) : trackService.getAllTracks();
            for (Track track : fallback) {
                if (candidateTracks.size() >= limit) {
                    break;
                }
                candidateTracks.add(track);
            }
        }
        if (candidateTracks.isEmpty()) {
            throw new RuntimeException("Недостаточно треков для формирования микса");
        }
        List<Track> mixTracks = new ArrayList<>(candidateTracks);
        if (mixTracks.size() > limit) {
            mixTracks = mixTracks.subList(0, limit);
        }
        Collections.shuffle(mixTracks);
        Playlist mix = new Playlist(name, description, user, Boolean.TRUE.equals(request.getMakePublic()));
        mix = playlistRepository.save(mix);
        int index = 0;
        for (Track track : mixTracks) {
            playlistTrackRepository.save(new PlaylistTrack(mix, track, index++));
        }
        return mix;
    }
    
    @Transactional(readOnly = true)
    public List<PlaylistTrackResponse> getPlaylistView(Long playlistId) {
        return playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlistId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private PlaylistTrackResponse toResponse(PlaylistTrack playlistTrack) {
        Track track = playlistTrack.getTrack();
        String artistName = track.getArtist() != null ? track.getArtist().getName() : null;
        String albumTitle = track.getAlbum() != null ? track.getAlbum().getTitle() : null;
        return new PlaylistTrackResponse(
                track.getId(),
                track.getTitle(),
                artistName,
                playlistTrack.getPosition(),
                albumTitle,
                track.getDurationSeconds(),
                track.getGenre());
    }
    
    private void persistPositions(List<PlaylistTrack> tracks) {
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).setPosition(i);
        }
    }
    
    private void normalizePositions(Long playlistId) {
        List<PlaylistTrack> ordered = playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        persistPositions(new ArrayList<>(ordered));
    }
}
