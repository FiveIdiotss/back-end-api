package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.Notification;
import com.mementee.api.dto.notificationDTO.NotificationDTO;
import com.mementee.api.repository.NotificationRepository;
import com.mementee.config.chat.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    private final RedisPublisher redisPublisher;
    private final static long TIMEOUT = 60L *  60;

    private NotificationDTO createNotificationDTO(Long receiverId, Object objectDTO, LocalDateTime localDateTime){
        return new NotificationDTO(receiverId, objectDTO, localDateTime);
    }

    @Transactional
    public void sendNotification(Long receiverId, Object objectDTO){
        Member receiver = memberService.findMemberById(receiverId);
        Notification notification = new Notification(receiver);
        NotificationDTO notificationDTO = createNotificationDTO(receiverId, objectDTO, notification.getCreatedAt());

        //sendToClient(receiverId, notificationDTO);

        redisPublisher.publish(ChannelTopic.of("notification:" + receiverId),
                notificationDTO);

        notificationRepository.saveNotification(notification);
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param receiverId - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(Long receiverId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        notificationRepository.save(receiverId, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> notificationRepository.deleteById(receiverId));

        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> notificationRepository.deleteById(receiverId));

        return emitter;
    }

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param memberId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = createEmitter(memberId);
        sendToClient(memberId, "EventStream Created. [memberId=" + memberId + "]");

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
    public void sendToClient(Long receiverId, Object data) {
        Optional<SseEmitter> emitter = notificationRepository.get(receiverId);
        if (emitter.isPresent()) {
            try {
                emitter.get().send(SseEmitter.event().id(String.valueOf(receiverId)).name("sse").data(data));
            } catch (IOException exception) {
                notificationRepository.deleteById(receiverId);
                emitter.get().completeWithError(exception);
            }
        }
    }
}
