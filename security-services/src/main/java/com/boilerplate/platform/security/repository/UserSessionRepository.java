package com.boilerplate.platform.security.repository;

import com.boilerplate.platform.security.entity.UserAccount;
import com.boilerplate.platform.security.entity.UserSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    long countByUser(UserAccount user);

    List<UserSession> findByUserOrderByLoggedInAtAsc(UserAccount user);

    Optional<UserSession> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);
}
