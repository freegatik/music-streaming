package ru.music.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.music.streaming.model.SessionStatus;
import ru.music.streaming.model.UserSession;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshToken(String refreshToken);

    Optional<UserSession> findByRefreshTokenAndStatus(String refreshToken, SessionStatus status);
}

