package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.music.streaming.dto.CreateUserRequest;
import ru.music.streaming.dto.RegistrationRequest;
import ru.music.streaming.dto.UserLibrarySummaryResponse;
import ru.music.streaming.model.Artist;
import ru.music.streaming.model.Playlist;
import ru.music.streaming.model.PlaylistTrack;
import ru.music.streaming.model.Role;
import ru.music.streaming.model.Track;
import ru.music.streaming.model.User;
import ru.music.streaming.repository.PlaylistRepository;
import ru.music.streaming.repository.PlaylistTrackRepository;
import ru.music.streaming.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository,
                       PlaylistRepository playlistRepository,
                       PlaylistTrackRepository playlistTrackRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public User registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с именем " + request.getUsername() + " уже существует");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с email " + request.getEmail() + " уже существует");
        }
        
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User createUserWithRole(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с именем " + request.getUsername() + " уже существует");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с email " + request.getEmail() + " уже существует");
        }
        
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
        
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));
    }
    
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Пользователь с email " + userDetails.getEmail() + " уже существует");
        }
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с email " + email + " не найден"));
    }
    
    @Transactional(readOnly = true)
    public UserLibrarySummaryResponse getUserLibrarySummary(Long userId) {
        User user = getUserById(userId);
        List<Playlist> playlists = playlistRepository.findByUserId(userId);
        List<PlaylistTrack> playlistTracks = playlistTrackRepository.findByUserId(userId);
        int totalTracks = playlistTracks.size();
        Set<Long> uniqueTrackIds = new HashSet<>();
        Set<Long> uniqueArtistIds = new HashSet<>();
        int totalDuration = 0;
        for (PlaylistTrack playlistTrack : playlistTracks) {
            Track track = playlistTrack.getTrack();
            if (track == null) {
                continue;
            }
            uniqueTrackIds.add(track.getId());
            Artist artist = track.getArtist();
            if (artist != null) {
                uniqueArtistIds.add(artist.getId());
            }
            totalDuration += Objects.requireNonNullElse(track.getDurationSeconds(), 0);
        }
        return new UserLibrarySummaryResponse(
                user.getId(),
                playlists.size(),
                totalTracks,
                uniqueTrackIds.size(),
                uniqueArtistIds.size(),
                totalDuration
        );
    }
}
