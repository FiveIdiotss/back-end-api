package com.mementee.api.repository;


import com.mementee.api.domain.RefreshToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final EntityManager em;

    public RefreshToken findOne(Long id) {
        return em.find(RefreshToken.class, id);
    }

    public void save(RefreshToken refreshToken){
        em.persist(refreshToken);
    }
    public Optional<RefreshToken> findRefreshTokenByEmail(String email) {
        try {
            RefreshToken refreshToken = em.createQuery("select r from RefreshToken r where r.email = :email", RefreshToken.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(refreshToken);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void deleteRefreshToken(Optional<RefreshToken> refreshToken){
        refreshToken.ifPresent(token -> em.createQuery("delete from RefreshToken r where r.id = :tokenId")
                .setParameter("tokenId", token.getId())
                .executeUpdate());
    }

    public Optional<RefreshToken> findRefreshTokenByRefreshToken(String token){
        try {
            RefreshToken refreshToken = em.createQuery("select r from RefreshToken r where r.refreshToken = :token", RefreshToken.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.ofNullable(refreshToken);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
