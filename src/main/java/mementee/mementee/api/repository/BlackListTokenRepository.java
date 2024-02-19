package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.BlackListToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BlackListTokenRepository {

    private final EntityManager em;

    //블랙 리스트에 access 등록
    public void save(BlackListToken bt) {
        em.persist(bt);
    }

    public Optional<BlackListToken> isCheckBlackList(String blackListToken) {
        try {
            BlackListToken bt = em.createQuery("select b from BlackListToken b where b.blackListToken = :blackListToken", BlackListToken.class)
                    .setParameter("blackListToken", blackListToken)
                    .getSingleResult();
            return Optional.ofNullable(bt);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
