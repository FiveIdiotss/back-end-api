package com.team.mementee.api.service;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.chat.ChatMessage;
import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.dto.chatDTO.ChatMessageRequest;
import com.team.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.team.mementee.api.dto.chatDTO.LatestMessageDTO;
import com.team.mementee.api.repository.chat.ChatMessageRepository;
import com.team.mementee.api.repository.chat.ChatRoomRepository;
import com.team.mementee.exception.notFound.ChatMessageNotFound;
import com.team.mementee.exception.notFound.ChatRoomNotFound;
import com.team.mementee.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final S3Service s3Service;
    private final RedisTemplate<String, Object> redisTemplate;

    public void userEnterChatRoom(Long chatRoomId, Long userId) {
        log.info(userId + "번 유저가 " + chatRoomId + "번 채팅방에 입장하였습니다.");
        String key = "chatRoom" + chatRoomId;
        redisTemplate.opsForSet().add(key, userId);
        // 읽지 않은 메시지 모두 읽음 처리
        markAllMessagesAsRead(chatRoomId, userId);
    }

    public void userLeaveChatRoom(Long chatRoomId, Long userId) {
        log.info(userId + "번 유저가 " + chatRoomId + "번 채팅방에서 퇴장하였습니다.");
        String key = "chatRoom" + chatRoomId;
        redisTemplate.opsForSet().remove(key, userId);
    }

    public Long getNumberOfUserInChatRoom(Long chatRoodId) {
        String key = "chatRoom" + chatRoodId;
        return redisTemplate.opsForSet().size(key);
    }

    public void setMessageReadCount(ChatMessageRequest request) {
        // If both users are in the chat room, set the readCount to 2.
        Long userNumber = getNumberOfUserInChatRoom(request.getChatRoomId());
        if (userNumber == 2) request.setReadCount(2);
    }

    @Transactional
    public void markAllMessagesAsRead(Long chatRoomId, Long userId) {
        chatMessageRepository.markMessageAsRead(chatRoomId, userId);
    }

    @Transactional
    public int getUnreadMessageCount(Long chatRoomId, Long loginMemberId) {
        return chatMessageRepository.getUnreadMessageCount(chatRoomId, loginMemberId);
    }

    @Transactional
    public ChatRoomDTO createChatRoomDTO(Long loginMemberId, ChatRoom chatRoom) {
        Member member = memberService.findMemberById(loginMemberId);
        Member receiver = getReceiver(loginMemberId, chatRoom);
        LatestMessageDTO latestMessageDTO = LatestMessageDTO.createLatestMessageDTO(findLatestChatMessage(chatRoom.getId()));
        int unreadMessageCount = getUnreadMessageCount(chatRoom.getId(), loginMemberId);
        return ChatRoomDTO.createChatRoomDTO(member, receiver, chatRoom, latestMessageDTO, unreadMessageCount,
                chatRoom.getMatching().getBoard().getId(), chatRoom.getMatching().getId());
    }

    @Transactional
    public void saveMessage(ChatMessageRequest request) {
        ChatMessage chatMessage = createMessageByChatMessageRequest(request);
        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void updateState(ChatRoom chatRoom){
        chatRoom.updateState(chatRoom);
    }

    @Transactional
    public void changeToComplete(ChatMessage chatMessage){
        chatMessage.changeToComplete();
    }

    public ChatMessage createMessageByChatMessageRequest(ChatMessageRequest request) {
        Member sender = memberService.findMemberById(request.getSenderId());
        ChatRoom chatRoom = findChatRoomById(request.getChatRoomId());
        return new ChatMessage(request.getMessageType(), request.getFileURL(), request.getContent(), sender, chatRoom, request.getLocalDateTime(), request.getReadCount());
    }

    //회원 조회 로직, memberId는 Sender
    public Member getReceiver(Long loginMemberId, ChatRoom chatRoom) {
        if (Objects.equals(loginMemberId, chatRoom.getSender().getId()))
            return chatRoom.getReceiver();
        return chatRoom.getSender();
    }

    public Optional<ChatMessage> findLatestChatMessage(Long chatRoomId) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        List<ChatMessage> messages = chatMessageRepository.findChatMessagesByChatRoom(chatRoom);
        if (!messages.isEmpty()) {
            messages.sort(Comparator.comparing(ChatMessage::getLocalDateTime).reversed());
            return Optional.of(messages.get(0));
        } else {
            log.info("No chatMessage exists in ChatRoom");
            return Optional.empty();
        }
    }

    public ChatRoom findChatRoomById(Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty())
            throw new ChatRoomNotFound();
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
        return chatRoomRepository.findChatRoomsByMemberId(memberId);
    }

    public String saveMultipartFile(MultipartFile file) {
        return s3Service.save(file);
    }
}