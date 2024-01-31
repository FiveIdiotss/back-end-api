package mementee.mementee.api.repository.chat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository {

    private final EntityManager em;

//    public void save(Chat)
}
