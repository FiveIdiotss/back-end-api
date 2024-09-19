package com.team.mementee.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.team.mementee.api.domain.*;
import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.domain.enumtype.NotificationType;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import com.team.mementee.api.dto.applyDTO.ApplyRequest;
import com.team.mementee.api.dto.applyDTO.ReasonOfRejectRequest;
import com.team.mementee.api.dto.chatDTO.ChatMessageRequest;
import com.team.mementee.api.dto.notificationDTO.FcmDTO;
import com.team.mementee.api.dto.notificationDTO.FcmMessage;
import com.team.mementee.api.dto.subBoardDTO.ReplyRequest;
import com.team.mementee.api.repository.fcm.FcmTokenRepository;
import com.team.mementee.exception.ServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
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
    private final ApplyService applyService;

    //title, otherPK 추가

    public FcmDTO createMatchinCompleteFcmDTO(String authorizationHeader, Long applyId){
        Member sender = memberService.findMemberByToken(authorizationHeader);
        Apply apply = applyService.findApplyById(applyId);
        Board board = apply.getBoard();
        Long receiverId = board.getMember().getId();
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                board.getTitle(), "수락", applyId, NotificationType.MATCHING_COMPLETE);
    }

    public FcmDTO createMatchingDeclineFcmDTO(String authorizationHeader, Long applyId, ReasonOfRejectRequest request){
        Member sender = memberService.findMemberByToken(authorizationHeader);
        Apply apply = applyService.findApplyById(applyId);
        Board board = apply.getBoard();
        Long receiverId = board.getMember().getId();
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                board.getTitle(), request.getContent(), applyId, NotificationType.MATCHING_DECLINE);
    }

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
        NotificationType notificationType;
        if(subBoard.getSubBoardType().equals(SubBoardType.QUEST)) notificationType = NotificationType.REPLY_QUEST;
        else notificationType = NotificationType.REPLY_REQUEST;
        return new FcmDTO(receiverId, sender.getId(), sender.getName(), sender.getMemberImageUrl(),
                subBoard.getTitle(), request.getContent(), subBoard.getId(), notificationType);
    }

    public FcmDTO createChatFcmDTO(ChatMessageRequest messageDTO){
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

    //알림 보내기
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
                                "type", fcmDTO.getNotificationType().name(),
                                "otherPK", String.valueOf(fcmDTO.getOtherPK())
                        ))
                        .build()).validateOnly(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

//    private String getAccessToken() throws IOException {
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//        googleCredentials.refreshIfExpired();
//
//        return googleCredentials.getAccessToken().getTokenValue();
//    }

    private String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new FileInputStream(firebaseConfigPath))
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        }catch (IOException e){
            throw new ServerErrorException();
        }
    }
}
