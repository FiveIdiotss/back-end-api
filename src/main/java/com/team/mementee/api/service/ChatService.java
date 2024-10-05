package com.team.mementee.api.service;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.chat.ChatMessage;
import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.dto.chatDTO.ChatMessageRequest;
import com.team.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.team.mementee.api.dto.chatDTO.LatestMessage;
import com.team.mementee.api.repository.chat.ChatMessageRepository;
import com.team.mementee.api.repository.chat.ChatRoomRepository;
import com.team.mementee.exception.notFound.ChatMessageNotFound;
import com.team.mementee.exception.notFound.ChatRoomNotFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    @Value("${api.chat.enter-url}")
    private String chatEnterUrl;
    @Value("${api.chat.leave-url}")
    private String chatLeaveUrl;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;

    public void userEnterChatRoom(Long chatRoomId, Long userId) {
        log.info(userId + "번 유저가 " + chatRoomId + "번 채팅방에 입장하였습니다.");
        String key = "chatRoom" + chatRoomId;
        redisTemplate.opsForSet().add(key, userId);
        // 읽지 않은 메시지 모두 읽음 처리
        markMessagesRead(chatRoomId, userId);
        // REST API 호출 예제
        callChatRoomEnterAPI(chatRoomId, userId);
    }

    public void userLeaveChatRoom(Long chatRoomId, Long userId) {
        log.info(userId + "번 유저가 " + chatRoomId + "번 채팅방에서 퇴장하였습니다.");
        String key = "chatRoom" + chatRoomId;
        redisTemplate.opsForSet().remove(key, userId);

        // 유저 퇴장 REST API 호출
        callChatRoomLeaveAPI(chatRoomId, userId);
    }

    private void callChatRoomEnterAPI(Long chatRoomId, Long userId) {
        webClient.post()
                .uri(chatEnterUrl)
                .bodyValue(Map.of("chatRoomId", chatRoomId, "userId", userId))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    // REST API 호출 메서드 추가
    private void callChatRoomLeaveAPI(Long chatRoomId, Long userId) {
        log.info(userId + "번 유저가 " + chatRoomId + "번 채팅방에서 퇴장하였습니다.");
        webClient.post()
                .uri(chatLeaveUrl)
                .bodyValue(Map.of("chatRoomId", chatRoomId, "userId", userId))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    public Long countUsersInChatRoom(Long chatRoodId) {
        String key = "chatRoom" + chatRoodId;
        return redisTemplate.opsForSet().size(key);
    }

    public void updateReadCount(ChatMessageRequest request) {
        // If both users are in the chat room, set the readCount to 2.
        Long userCount = countUsersInChatRoom(request.getChatRoomId());
        request.updateReadCount(userCount);
    }

    @Transactional
    public void markMessagesRead(Long chatRoomId, Long userId) {
        chatMessageRepository.markMessagesRead(chatRoomId, userId);
    }

    @Transactional
    public int countUnreadMessages(Long chatRoomId, Long userId) {
        // userId 유저가 안읽은 메시지 개수 반환.
        return chatMessageRepository.countUnreadMessages(chatRoomId, userId);
    }

    @Transactional
    public void saveMessage(ChatMessageRequest request) {
        ChatMessage chatMessage = createMessageFromRequest(request);
        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void updateState(ChatRoom chatRoom) {
        chatRoom.updateState(chatRoom);
    }

    @Transactional
    public void changeToComplete(ChatMessage chatMessage) {
        chatMessage.changeToComplete();
    }

    public ChatRoomDTO createChatRoomDTO(Long userId, ChatRoom chatRoom) {
        Member member = memberService.findMemberById(userId);
        Member receiver = getReceiver(userId, chatRoom);
        LatestMessage latestMessage = LatestMessage.of(getLatestMessage(chatRoom.getId()));
        int unreadCount = countUnreadMessages(chatRoom.getId(), userId);

        return ChatRoomDTO.of(member, receiver, chatRoom, latestMessage, unreadCount);
    }

    public ChatMessage createMessageFromRequest(ChatMessageRequest request) {
        Member sender = memberService.findMemberById(request.getSenderId());
        ChatRoom chatRoom = findChatRoomById(request.getChatRoomId());
        return new ChatMessage(request, sender, chatRoom);
    }

    //회원 조회 로직, memberId는 Sender
    public Member getReceiver(Long userId, ChatRoom chatRoom) {
        if (Objects.equals(userId, chatRoom.getSender().getId())) return chatRoom.getReceiver();
        return chatRoom.getSender();
    }

    public ChatMessage getLatestMessage(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findChatMessagesByChatRoomId(chatRoomId);

        if (messages.isEmpty()) return null;

        messages.sort(Comparator.comparing(ChatMessage::getLocalDateTime).reversed());
        return messages.get(0); // 최신 메시지를 반환
    }

    public ChatRoom findChatRoomById(Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty()) throw new ChatRoomNotFound();
        return chatRoom.get();
    }

    public ChatMessage findChatMessageById(Long chatId) {
        Optional<ChatMessage> chatMessage = chatMessageRepository.findById(chatId);
        if (chatMessage.isEmpty())
            throw new ChatMessageNotFound();
        return chatMessage.get();
    }

    // 채팅방 ID로 채팅방 메세지 조회
    public Slice<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        return chatMessageRepository.findChatMessagesByChatRoom(chatRoom, pageable);
    }

    // 특정 멤버가 속한 모든 채팅방 조회
    public List<ChatRoom> findAllChatRoomByMemberId(Long memberId) {
        return chatRoomRepository.findAllChatRoomByMemberId(memberId);
    }

}