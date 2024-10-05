package com.team.mementee.api.controller.chat;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.chat.ChatMessage;
import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.domain.enumtype.DecisionStatus;
import com.team.mementee.api.domain.enumtype.ExtendState;
import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.chatDTO.*;
import com.team.mementee.api.dto.notificationDTO.FcmDTO;
import com.team.mementee.api.service.*;
import com.team.mementee.exception.ForbiddenException;
import com.team.mementee.exception.conflict.ExtendRequestConflictException;
import com.team.mementee.exception.conflict.ExtendResponseConflictException;
import com.team.mementee.exception.unauthorized.UnauthorizedException;
import com.team.mementee.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    @Value("${websocket.unread-path}")
    private String websocketUnreadPath;

    @Value("${websocket.chat-path}")
    private String websocketChatPath;

    private final ChatService chatService;
    private final MatchingService matchingService;
    private final MemberService memberService;
    private final FcmService fcmService;
    private final FileService fileService;
    private final S3Service s3Service;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate websocketPublisher;

    @MessageMapping("/hello")
    private void hello(Message<ChatMessageRequest> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Member member = (Member) accessor.getSessionAttributes().get("member");
        Long chatRoomId = (Long) accessor.getSessionAttributes().get("chatRoomId");

        ChatMessageRequest request = message.getPayload().updateChatRequest(member, chatRoomId);
        processChatMessage(request);
    }

    private void processChatMessage(ChatMessageRequest request) {
        // 웹소켓 세션에서 senderId와 chatRoomId 읽어오기
        Long senderId = request.getSenderId();
        Long chatRoomId = request.getChatRoomId();

        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        Member receiver = chatService.getReceiver(senderId, chatRoom);

        chatService.updateReadCount(request);
        chatService.saveMessage(request);

        // Send WebSocket notifications
        sendWebSocketNotifications(request, chatRoomId, receiver.getId());

        // FCM notification
        FcmDTO fcmDTO = fcmService.createChatFcmDTO(request);
        fcmService.sendMessageTo(fcmDTO);

        notificationService.sendTotalChatCount(receiver.getId());
    }

    private void sendWebSocketNotifications(ChatMessageRequest request, Long chatRoomId, Long receiverId) {
        // send message
        websocketPublisher.convertAndSend(websocketChatPath + chatRoomId, request);

        updateChatRoomInfo(chatRoomId, receiverId);
    }

    private void updateChatRoomInfo(Long chatRoomId, Long receiverId) {
        LatestMessage lastMessage = LatestMessage.of(chatService.getLatestMessage(chatRoomId));
        int unreadCount = chatService.countUnreadMessages(chatRoomId, receiverId);
        ChatUpdateDTO chatUpdateDTO = new ChatUpdateDTO(chatRoomId, unreadCount, lastMessage);
        websocketPublisher.convertAndSend(websocketUnreadPath + chatRoomId, chatUpdateDTO);
    }

    @Operation(summary = "사용자 채팅방 입장", description = "지정된 채팅방 ID와 사용자 ID를 사용하여 사용자가 채팅방에 입장했음을 알리는 메시지를 WebSocket을 통해 전송합니다.")
    @PostMapping("/enter")
    public void userEnterChatRoom(@RequestBody Map<String, Long> requestBody) {
        Long chatRoomId = requestBody.get("chatRoomId");
        Long userId = requestBody.get("userId");

        ChatMessageRequest enterRequest = ChatMessageRequest.createUserEnterChatRequest(memberService.findMemberById(userId), chatRoomId);
        chatService.updateReadCount(enterRequest);

        websocketPublisher.convertAndSend(websocketChatPath + chatRoomId, enterRequest);
    }

    @Operation(summary = "사용자 채팅방 퇴장", description = "지정된 채팅방 ID와 사용자 ID를 사용하여 사용자가 채팅방에서 퇴장했음을 알리는 메시지를 WebSocket을 통해 전송합니다.")
    @PostMapping("/leave")
    public void userLeaveChatRoom(@RequestBody Map<String, Long> requestBody) {
        Long chatRoomId = requestBody.get("chatRoomId");
        Long userId = requestBody.get("userId");

        // 퇴장 메시지 생성
        ChatMessageRequest leaveRequest = ChatMessageRequest.createUserLeaveChatRequest(memberService.findMemberById(userId), chatRoomId);
        chatService.updateReadCount(leaveRequest);  // 읽음 처리 (필요한 경우)

        // WebSocket을 통해 퇴장 메시지를 전송
        websocketPublisher.convertAndSend(websocketChatPath + chatRoomId, leaveRequest);
    }

    @Operation(description = "파일 전송 처리")
    @PostMapping("/sendFile")
    public CommonApiResponse<?> sendFileInChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestPart(value = "file") MultipartFile file,
                                                   @RequestParam Long chatRoomId) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        ChatMessageRequest request = ChatMessageRequest.of(
                fileService.extractFileType(file),
                s3Service.saveMultipartFile(file),
                file.getOriginalFilename(),
                loginMember,
                chatRoomId);

        chatService.updateReadCount(request);

        // If a file that has supported contentType is uploaded, save the file in S3 and return the URL.
        chatService.saveMessage(request);
        websocketPublisher.convertAndSend(websocketChatPath + request.getChatRoomId(), request);

        //FCM 알림
        FcmDTO fcmDTO = fcmService.createChatFcmDTO(request);
        fcmService.sendMessageTo(fcmDTO);

        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public CommonApiResponse<?> findAllMessagesByChatRoom(@RequestParam("page") int page,
                                                          @RequestParam("size") int size,
                                                          @PathVariable("chatRoomId") Long chatRoomId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); // 내림차순(최신순)
        Slice<ChatMessageDTO> map = chatService.findAllMessagesByChatRoomId(chatRoomId, pageable).map(ChatMessageDTO::of);
        return CommonApiResponse.createSuccess(map);
    }

    @Operation(summary = "상대방 ID로 해당 채팅방 조회. 상대방 프로필을 조회하고 메시지를 보낼 때, 둘 사이에 채팅방이 존재하는지 확인. 존재 하지 않으면 null 반환")
    @GetMapping("/chatRoom")
    public CommonApiResponse<ChatRoomDTO> findChatRoomById(
            @RequestParam Long chatRoomId,
            @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);

        validateChatRoomAccess(loginMember, chatRoom);

        ChatRoomDTO chatRoomDTO = chatService.createChatRoomDTO(loginMember.getId(), chatRoom);
        return CommonApiResponse.createSuccess(chatRoomDTO);
    }

    @Operation(summary = "특정 멤버가 속한 채팅방 모두 조회")
    @GetMapping("/chatRooms")
    public CommonApiResponse<List<ChatRoomDTO>> findAllChatRoomsByMemberId(
            @RequestHeader("Authorization") String authorizationHeader, HttpSession session) {
        String email = (String) session.getAttribute("email");
        Member member = memberService.findMemberByToken(authorizationHeader);
        List<ChatRoomDTO> chatRoomDTOs = chatService.findAllChatRoomByMemberId(member.getId()).stream()
                .map(chatRoom -> chatService.createChatRoomDTO(member.getId(), chatRoom))
                .toList();

        return CommonApiResponse.createSuccess(chatRoomDTOs);
    }

    //상담 연장 요청
    @Operation(summary = "상담 연장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PostMapping("/extend/request/{chatRoomId}")
    public CommonApiResponse<?> extendMatching(@RequestHeader("Authorization") String authorizationHeader,
                                               @PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);

        if (chatRoom.getExtendState() == ExtendState.WAITING)
            throw new ExtendRequestConflictException();

        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        Member mentee = chatRoom.getMatching().getMentee();
        if (!loginMember.equals(mentee))
            throw new ForbiddenException();

        ChatMessageRequest request = ChatMessageRequest.createExtendRequest(loginMember, chatRoomId);
        processChatMessage(request);

        chatService.updateState(chatRoom);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "상담 연장 수락/거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PostMapping("/extend/{chatId}")
    public CommonApiResponse<?> extendAcceptOrDecline(@RequestHeader("Authorization") String authorizationHeader,
                                                      @PathVariable Long chatId,
                                                      @RequestParam DecisionStatus status) {
        ChatMessage chatMessage = chatService.findChatMessageById(chatId);
        ChatRoom chatRoom = chatMessage.getChatRoom();

        if (chatRoom.getExtendState() == ExtendState.EMPTY)
            throw new ExtendResponseConflictException();

        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        Member mentor = chatRoom.getMatching().getMentor();
        if (!loginMember.equals(mentor))
            throw new ForbiddenException();

        ChatMessageRequest messageDTO = ChatMessageRequest.createExtendResponse(status, loginMember, chatRoom.getId());
        processChatMessage(messageDTO);

        chatService.updateState(chatRoom);
        chatService.changeToComplete(chatMessage);

        if (status.equals(DecisionStatus.ACCEPT))
            matchingService.extendConsultTime(chatRoom.getMatching());

        return CommonApiResponse.createSuccess();
    }

    private void validateChatRoomAccess(Member loginMember, ChatRoom chatRoom) {
        boolean isSender = Objects.equals(loginMember.getId(), chatRoom.getSender().getId());
        boolean isReceiver = Objects.equals(loginMember.getId(), chatRoom.getReceiver().getId());

        if (!isSender && !isReceiver) {
            throw new UnauthorizedException();
        }
    }

    private void test(Message<ChatMessageRequest> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 반복 횟수 설정
        final int TEST_COUNT = 10000;

        long totalDurationWithSession = 0;
        for (int i = 0; i < TEST_COUNT; i++) {
            long startTime2 = System.nanoTime(); // 시작 시간 기록
            Member member = (Member) accessor.getSessionAttributes().get("member");
            Long chatRoomId = (Long) accessor.getSessionAttributes().get("chatRoomId");
            long endTime2 = System.nanoTime(); // 종료 시간 기록
            long duration2 = endTime2 - startTime2;
            totalDurationWithSession += duration2;
        }
        double averageDurationWithSession = totalDurationWithSession / (double) TEST_COUNT;
        log.info("Average execution time with session: {} ns", averageDurationWithSession);

        // 세션을 사용하지 않는 경우
        long totalDurationWithoutSession = 0;
        for (int i = 0; i < TEST_COUNT; i++) {
            long startTime = System.nanoTime(); // 시작 시간 기록
            String token = accessor.getFirstNativeHeader("Authorization");
            String chatRoomIdHeader = accessor.getFirstNativeHeader("chatRoomId");

            if (token != null && chatRoomIdHeader != null) {
                Member member = memberService.findMemberByToken(token);
                Long chatRoomId = Long.parseLong(chatRoomIdHeader);
            }
            long endTime = System.nanoTime(); // 종료 시간 기록
            long duration = endTime - startTime;
            totalDurationWithoutSession += duration;
        }
        double averageDurationWithoutSession = totalDurationWithoutSession / (double) TEST_COUNT;
        log.info("Average execution time without session: {} ns", averageDurationWithoutSession);

        // 성능 향상 비율 계산
        double improvementPercentage = ((averageDurationWithoutSession - averageDurationWithSession) / averageDurationWithoutSession) * 100;
        log.info("Performance improvement using session: {}%", improvementPercentage);
    }

}
