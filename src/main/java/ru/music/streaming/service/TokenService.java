package ru.music.streaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import ru.music.streaming.exception.TokenException;
import ru.music.streaming.model.SessionStatus;
import ru.music.streaming.model.User;
import ru.music.streaming.model.UserSession;
import ru.music.streaming.repository.UserRepository;
import ru.music.streaming.repository.UserSessionRepository;
import ru.music.streaming.security.JwtTokenProvider;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TokenService(UserSessionRepository sessionRepository, JwtTokenProvider tokenProvider, UserRepository userRepository, PlatformTransactionManager transactionManager) {
        this.sessionRepository = sessionRepository;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactionTemplate = new TransactionTemplate(transactionManager, def);
    }

    @Transactional
    public UserSession createSession(User user, String deviceId) {
        UUID sessionId = UUID.randomUUID();
        String username = user.getUsername();
        String email = user.getEmail();
        String role = user.getRole().name();

        String accessToken = tokenProvider.generateAccessToken(username, email, role);
        String refreshToken = tokenProvider.generateRefreshToken(username, email, sessionId.toString());

        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(tokenProvider.getAccessTokenExpiration());
        Instant refreshExpiry = now.plusMillis(tokenProvider.getRefreshTokenExpiration());

        UserSession session = new UserSession(
                user.getEmail(),
                deviceId,
                accessToken,
                refreshToken,
                accessExpiry,
                refreshExpiry,
                SessionStatus.ACTIVE
        );

        return sessionRepository.save(session);
    }

    @Transactional
    public UserSession refreshTokens(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new TokenException("Невалидный refresh токен");
        }

        UserSession session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenException("Сессия не найдена"));

        if (session.getStatus() == SessionStatus.USED) {
            revokeSessionInNewTransaction(session.getId());
            throw new TokenException("Обнаружена попытка повторного использования refresh токена");
        }

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new TokenException("Сессия неактивна");
        }

        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) {
            revokeSessionInNewTransaction(session.getId());
            throw new TokenException("Refresh токен истек");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        String username = tokenProvider.getUsernameFromToken(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenException("Пользователь не найден"));
        String role = user.getRole().name();

        session.setStatus(SessionStatus.USED);
        sessionRepository.save(session);

        UUID newSessionId = UUID.randomUUID();
        String newAccessToken = tokenProvider.generateAccessToken(username, email, role);
        String newRefreshToken = tokenProvider.generateRefreshToken(username, email, newSessionId.toString());

        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(tokenProvider.getAccessTokenExpiration());
        Instant refreshExpiry = now.plusMillis(tokenProvider.getRefreshTokenExpiration());

        UserSession newSession = new UserSession(
                email,
                session.getDeviceId(),
                newAccessToken,
                newRefreshToken,
                accessExpiry,
                refreshExpiry,
                SessionStatus.ACTIVE
        );

        return sessionRepository.save(newSession);
    }

    public boolean validateRefreshTokenAndSession(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            return false;
        }

        return sessionRepository.findByRefreshTokenAndStatus(refreshToken, SessionStatus.ACTIVE)
                .map(session -> session.getRefreshTokenExpiry().isAfter(Instant.now()))
                .orElse(false);
    }


    public void revokeSessionInNewTransaction(UUID sessionId) {
        transactionTemplate.executeWithoutResult(status -> {
            UserSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null) {
                session.setStatus(SessionStatus.REVOKED);
                sessionRepository.saveAndFlush(session);
            }
        });
    }

    @Transactional
    public void revokeAllUserSessions(String userEmail) {
        sessionRepository.findAll().stream()
                .filter(session -> userEmail.equals(session.getUserEmail()))
                .filter(session -> session.getStatus() == SessionStatus.ACTIVE)
                .forEach(session -> {
                    session.setStatus(SessionStatus.REVOKED);
                    sessionRepository.save(session);
                });
    }
}

