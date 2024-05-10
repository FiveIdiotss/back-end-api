package com.mementee.api.repository;

import com.mementee.api.domain.Notification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final EntityManager em;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void saveNotification(Notification notification){
        em.persist(notification);
    }

    /**
     * 주어진 아이디와 이미터를 저장
     *
     * @param memberId      - 사용자 아이디.
     * @param emitter - 이벤트 Emitter.
     */
    public SseEmitter save(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);
        return emitter;
    }

    /**
     * 주어진 아이디의 Emitter를 제거
     *
     * @param memberId - 사용자 아이디.
     */
    public void deleteById(Long memberId) {
        emitters.remove(memberId);
        //lastEventIds.remove(memberId);
    }

    /**
     * 주어진 아이디의 Emitter를 가져옴.
     *
     * @param memberId - 사용자 아이디.
     * @return SseEmitter - 이벤트 Emitter.
     */
    public Optional<SseEmitter> get(Long memberId) {
        return Optional.ofNullable(emitters.get(memberId));
    }
}
