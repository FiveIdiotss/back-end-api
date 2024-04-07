package com.mementee.api.repository;

import com.mementee.api.domain.Notification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final EntityManager em;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, String> lastEventIds = new ConcurrentHashMap<>();
    private final List<Notification> notifications = new ArrayList<>();

    public void saveNotification(Notification notification){
        em.persist(notification);
    }

    /**
     * 주어진 아이디와 이미터를 저장
     *
     * @param memberId      - 사용자 아이디.
     * @param emitter - 이벤트 Emitter.
     */
    public void save(Long memberId, SseEmitter emitter, String lastEventId) {
        emitters.put(memberId, emitter);
        lastEventIds.put(memberId, lastEventId);
    }

    /**
     * 주어진 아이디의 Emitter를 제거
     *
     * @param memberId - 사용자 아이디.
     */
    public void deleteById(Long memberId) {
        emitters.remove(memberId);
        lastEventIds.remove(memberId);
    }

    /**
     * 주어진 아이디의 Emitter를 가져옴.
     *
     * @param memberId - 사용자 아이디.
     * @return SseEmitter - 이벤트 Emitter.
     */
    public SseEmitter get(Long memberId) {
        return emitters.get(memberId);
    }

    public String getLastEventId(Long memberId) {
        return lastEventIds.get(memberId);
    }

    // 특정 memberId에 대한 lastEventId 이후의 알림만 조회
    public List<Notification> findNotificationsAfter(Long memberId, String lastEventId) {
        return notifications.stream()
                .filter(notification -> notification.getReceiver().getId().equals(memberId) &&
                        Long.parseLong(notification.getId().toString()) > Long.parseLong(lastEventId))
                .collect(Collectors.toList());
    }
}
