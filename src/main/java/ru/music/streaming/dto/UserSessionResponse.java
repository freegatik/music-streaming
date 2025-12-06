package ru.music.streaming.dto;

import ru.music.streaming.model.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public class UserSessionResponse {
    private UUID id;
    private String userEmail;
    private String deviceId;
    private Instant accessTokenExpiry;
    private Instant refreshTokenExpiry;
    private SessionStatus status;

    public UserSessionResponse() {
    }

    public UserSessionResponse(UUID id, String userEmail, String deviceId, 
                               Instant accessTokenExpiry, Instant refreshTokenExpiry, SessionStatus status) {
        this.id = id;
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Instant getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(Instant accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public Instant getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Instant refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}

