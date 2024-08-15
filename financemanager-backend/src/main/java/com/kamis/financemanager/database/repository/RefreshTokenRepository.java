package com.kamis.financemanager.database.repository;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.RefreshToken;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>, JpaSpecificationExecutor<Loan> {

    /**
     * Finds a RefreshToken by its token
     * @param token The token of the RefreshToken to search for
     * @return An Optional RefreshToken if one was found
     */
    public Optional<RefreshToken> findByToken(UUID token);

    /**
     * Deletes all expired tokens from the database
     */
    @Query(value = "DELETE FROM refresh_tokens WHERE expiration_dt < CURRENT_TIMESTAMP", nativeQuery = true)
    public void deleteExpired();

    /**
     * Deletes all refresh tokens for a given user
     * @param username The username to delete tokens for
     */
    void deleteByUsername(String username);
}
