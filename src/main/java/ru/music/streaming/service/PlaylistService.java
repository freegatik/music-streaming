package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.model.Track;
import ru.music.streaming.model.User;
import ru.music.streaming.repository.PlaylistRepository;
import ru.music.streaming.repository.PlaylistTrackRepository;

import java.util.List;

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
        
        // Если позиция не указана, добавляем в конец
        if (position == null) {
            Integer maxPosition = playlistTrackRepository.findMaxPositionByPlaylistId(playlistId);
            position = (maxPosition == null) ? 0 : maxPosition + 1;
        }
        
        // Проверяем, что позиция свободна
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
    }
    
    public List<PlaylistTrack> getPlaylistTracks(Long playlistId) {
        return playlistTrackRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
    }
}
