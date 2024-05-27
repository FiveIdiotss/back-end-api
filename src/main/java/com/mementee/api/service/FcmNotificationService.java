package com.mementee.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Notification;
import com.mementee.api.domain.FcmToken;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.NotificationType;
import com.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.dto.notificationDTO.FcmMessage;
import com.mementee.api.repository.fcm.NotificationRepository;
import com.mementee.api.repository.fcm.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {

    @Value("${firebase.config-path}")
    private String firebaseConfigPath;

    @Value("${firebase.url}")
    private String API_URL;

    private final ObjectMapper objectMapper;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;

    private final ChatService chatService;
    private final MemberService memberService;
    private final BoardService boardService;

    public FcmDTO createApplyFcmDTO(String authorizationHeader, Long boardId, ApplyRequest request){
        Member sender = memberService.findMemberByToken(authorizationHeader);

        Board board = boardService.findBoardById(boardId);
        Long receiverId = board.getMember().getId();
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                request.getContent(), NotificationType.APPLY);
    }

    public FcmDTO createChatFcmDTO(ChatMessageDTO messageDTO){
        Member sender = memberService.findMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = chatService.findChatRoomById(messageDTO.getChatRoomId());
        Member receiver = chatService.getReceiver(messageDTO.getSenderId(), chatRoom);
        return new FcmDTO(receiver.getId(), sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                messageDTO.getContent(), NotificationType.CHAT);
    }

    public Page<Notification> findFcmDetailsByReceiverMember(String authorizationHeader, Pageable pageable){
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        return notificationRepository.findFcmDetailsByReceiveMember(loginMember, pageable);
    }

    @Transactional
    public void saveFCMNotification(Member member, String token) {
        Optional<FcmToken> fcmNotification = fcmTokenRepository.findFCMNotificationByMember(member);
        if(fcmNotification.isEmpty())
            fcmTokenRepository.save(new FcmToken(token, member));
        else
            fcmNotification.get().updateFCMToken(token);
    }

    @Transactional
    public void saveFcmDetail(FcmDTO fcmDTO) {
        Member targetMember = memberService.findMemberById(fcmDTO.getTargetMemberId());
        Member sendMember = memberService.findMemberById(fcmDTO.getSenderId());
        Notification notification = new Notification(fcmDTO.getContent(),
                fcmDTO.getNotificationType(), sendMember, targetMember);
        notificationRepository.save(notification);
    }


    //채팅 알림 보내기
    public void sendMessageTo(FcmDTO fcmDTO){
        try {
            Member member = memberService.findMemberById(fcmDTO.getTargetMemberId());
            Optional<FcmToken> fcmNotification = fcmTokenRepository.findFCMNotificationByMember(member);

            if (fcmNotification.isEmpty())
                return;

            String targetToken = fcmNotification.get().getToken();
            String message = makeMessage(targetToken, fcmDTO);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message,
                    MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request).execute();
            log.info(response.body().string());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String makeMessage(String targetToken, FcmDTO fcmDTO) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .data(Map.of(
                                "senderName", fcmDTO.getSenderName(),
                                "content", fcmDTO.getContent(),
                                "senderImageUrl", fcmDTO.getSenderImageUrl(),
                                "senderId", String.valueOf(fcmDTO.getSenderId()),
                                "type", fcmDTO.getNotificationType().name()
                        ))
                        .build()).validateOnly(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
