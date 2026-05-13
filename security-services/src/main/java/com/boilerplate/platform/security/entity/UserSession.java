package com.boilerplate.platform.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private String deviceName;

    @Column(nullable = false, updatable = false)
    private Instant loggedInAt;

    @Column(nullable = false)
    private Instant lastSeenAt;

    protected UserSession() {
    }

    public UserSession(UserAccount user, String tokenHash, String deviceName) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.deviceName = deviceName;
        this.loggedInAt = Instant.now();
        this.lastSeenAt = this.loggedInAt;
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Instant getLoggedInAt() {
        return loggedInAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void markSeen() {
        this.lastSeenAt = Instant.now();
    }
}
