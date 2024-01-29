package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Friend;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final EntityManager em;

    //친구 등록
    public void saveFriend(Friend friend){
        em.persist(friend);
    }


}
