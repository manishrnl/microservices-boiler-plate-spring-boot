package com.boilerplate.platform.security.service;

import com.boilerplate.platform.security.dto.AuthResponse;
import com.boilerplate.platform.security.dto.LoginRequest;
import com.boilerplate.platform.security.dto.SessionResponse;
import com.boilerplate.platform.security.dto.SignupRequest;
import com.boilerplate.platform.security.entity.UserAccount;
import com.boilerplate.platform.security.entity.UserSession;
import com.boilerplate.platform.security.repository.UserAccountRepository;
import com.boilerplate.platform.security.repository.UserSessionRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int maxSessionsPerUser;

    public AuthService(
            UserAccountRepository userAccountRepository,
            UserSessionRepository userSessionRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.max-sessions-per-user:5}") int maxSessionsPerUser
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.maxSessionsPerUser = maxSessionsPerUser;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (userAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserAccount user = new UserAccount(
                request.fullName(),
                email,
                passwordEncoder.encode(request.password())
        );
        UserAccount savedUser = userAccountRepository.save(user);
        return createSession(savedUser, request.deviceName());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return createSession(user, request.deviceName());
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> listSessions(String accessToken) {
        UserSession session = findSession(accessToken);
        return userSessionRepository.findByUserOrderByLoggedInAtAsc(session.getUser()).stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Transactional
    public void logout(String accessToken) {
        userSessionRepository.deleteByTokenHash(hashToken(accessToken));
    }

    private AuthResponse createSession(UserAccount user, String deviceName) {
        evictOldestSessions(user);
        String token = generateToken();
        UserSession session = userSessionRepository.save(new UserSession(user, hashToken(token), deviceName));
        int activeSessions = (int) userSessionRepository.countByUser(user);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                session.getId(),
                token,
                session.getDeviceName(),
                activeSessions,
                session.getLoggedInAt()
        );
    }

    private void evictOldestSessions(UserAccount user) {
        List<UserSession> sessions = userSessionRepository.findByUserOrderByLoggedInAtAsc(user);
        while (sessions.size() >= maxSessionsPerUser) {
            UserSession oldest = sessions.remove(0);
            userSessionRepository.delete(oldest);
        }
    }

    private UserSession findSession(String accessToken) {
        return userSessionRepository.findByTokenHash(hashToken(accessToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid session token"));
    }

    private SessionResponse toSessionResponse(UserSession session) {
        return new SessionResponse(
                session.getId(),
                session.getDeviceName(),
                session.getLoggedInAt(),
                session.getLastSeenAt()
        );
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
