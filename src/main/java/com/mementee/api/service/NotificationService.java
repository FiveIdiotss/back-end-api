package com.mementee.api.service;

import com.mementee.api.domain.Notification;
import com.mementee.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final static long TIMEOUT = 60L *  60 * 1000;

    @Transactional
    public void save(Notification notification){
        notificationRepository.saveNotification(notification);
    }

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param memberId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        if (lastEventId == null || lastEventId.isEmpty()) {
            lastEventId = "0"; // "0"으로 설정하여 최근 알림만을 전송하도록 함
        }

        SseEmitter emitter = createEmitter(memberId, lastEventId);
        sendToClient(memberId, "EventStream Created. [memberId=" + memberId + "]");

        // 필터링된 알림 조회 및 전송
        List<Notification> filteredNotifications = notificationRepository.findNotificationsAfter(memberId, lastEventId);
        for (Notification notification : filteredNotifications) {
            sendToClient(memberId, notification.getChatMessage()); // eventData를 적절히 전송가능한 형태로 변환 필요
        }

        return emitter;
    }

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param memberId - 메세지를 전송할 사용자의 아이디.
     * @param event  - 전송할 이벤트 객체.
     */
    public void notify(Long memberId, Object event) {
        sendToClient(memberId, event);
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param receiverId   - 데이터를 받을 사용자의 아이디.
     * @param data - 전송할 데이터.
     */
    private void sendToClient(Long receiverId, Object data) {
        SseEmitter emitter = notificationRepository.get(receiverId);
        System.out.println(emitter);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(receiverId)).name("sse").data(data));
            } catch (IOException exception) {
                notificationRepository.deleteById(receiverId);
                emitter.completeWithError(exception);
            }
        }
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param receiverId - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(Long receiverId, String lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        notificationRepository.save(receiverId, emitter, lastEventId);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> notificationRepository.deleteById(receiverId));

        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> notificationRepository.deleteById(receiverId));

        return emitter;
    }
}

