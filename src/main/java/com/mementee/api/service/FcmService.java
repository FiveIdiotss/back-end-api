package com.mementee.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.FcmToken;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.NotificationType;
import com.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.dto.notificationDTO.FcmMessage;
import com.mementee.api.dto.subBoardDTO.ReplyRequest;
import com.mementee.api.repository.fcm.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
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
public class FcmService {

    @Value("${firebase.config-path}")
    private String firebaseConfigPath;

    @Value("${firebase.url}")
    private String API_URL;

    private final ObjectMapper objectMapper;
    private final FcmTokenRepository fcmTokenRepository;

    private final ChatService chatService;
    private final MemberService memberService;
    private final BoardService boardService;
    private final SubBoardService subBoardService;


    //title, otherPK 추가
    public FcmDTO createApplyFcmDTO(String authorizationHeader, Long boardId, ApplyRequest request){
        Member sender = memberService.findMemberByToken(authorizationHeader);
        Board board = boardService.findBoardById(boardId);
        Long receiverId = board.getMember().getId();
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                board.getTitle(), request.getContent(), boardId, NotificationType.APPLY);
    }

    public FcmDTO createReplyFcmDTO(String authorizationHeader, SubBoard subBoard, ReplyRequest request){
        Member sender = memberService.findMemberByToken(authorizationHeader);
        Long receiverId = subBoard.getMember().getId();
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                subBoard.getTitle(), request.getContent(), subBoard.getId(), NotificationType.REPLY);
    }

    public FcmDTO createChatFcmDTO(ChatMessageDTO messageDTO){
        Member sender = memberService.findMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = chatService.findChatRoomById(messageDTO.getChatRoomId());
        Member receiver = chatService.getReceiver(messageDTO.getSenderId(), chatRoom);
        return new FcmDTO(receiver.getId(), sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                "채팅 도착", messageDTO.getContent(), chatRoom.getId(), NotificationType.CHAT);
    }

    @Transactional
    public void saveFCMToken(Member member, String token) {
        Optional<FcmToken> fcmToken = fcmTokenRepository.findFcmTokenByMember(member);
        if(fcmToken.isEmpty())
            fcmTokenRepository.save(new FcmToken(token, member));
        else
            fcmToken.get().updateFCMToken(token);
    }

    //채팅 알림 보내기
    public void sendMessageTo(FcmDTO fcmDTO){
        try {
            Member member = memberService.findMemberById(fcmDTO.getTargetMemberId());
            Optional<FcmToken> fcmNotification = fcmTokenRepository.findFcmTokenByMember(member);

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
