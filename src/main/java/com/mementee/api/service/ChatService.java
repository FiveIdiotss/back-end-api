package com.mementee.api.service;

import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.LatestMessageDTO;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.repository.chat.ChatRoomRepositorySub;
import com.mementee.config.chat.RedisSubscriber;
import com.mementee.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
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
    private final ChatRoomRepositorySub chatRoomRepositorySub;
    private final MemberService memberService;
    private final S3Service s3Service;
    private final RedisTemplate<String, Object> redisTemplate;

    //

    public void userEnterChatRoom(Long chatRoomId, Long userId) {
        String key = "chatRoom:" + chatRoomId;
        redisTemplate.opsForSet().add(key, userId);
        // 읽지 않은 메시지 모두 읽음 처리
        markAllMessagesAsRead(chatRoomId, userId);
    }

    public void userLeaveChatRoom(Long chatRoomId, Long userId) {
        String key = "chatRoom:" + chatRoomId;
        redisTemplate.opsForSet().remove(key, userId);
    }

    public boolean isUserInChatRoom(Long chatRoomId, Long userId) {
        String key = "chatRoom:" + chatRoomId;
        return redisTemplate.opsForSet().isMember(key, userId);
    }

    public Set<Object> getUsersInChatRoom(Long chatRoomId) {
        String key = "chatRoom:" + chatRoomId;
        return redisTemplate.opsForSet().members(key);
    }

    public void markAllMessagesAsRead(Long chatRoomId, Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndReadCountGreaterThan(chatRoomId, 0);
        for (ChatMessage message : messages) {
            if (!message.getSender().getId().equals(userId) && message.getReadCount() > 0) {
                message.setReadCount(0);
                chatMessageRepository.save(message);
            }
        }
    }



    //

    //회원 조회 로직, memberId는 Sender
    public Long getReceiverId(Long memberId, ChatRoom chatRoom) {
        if (Objects.equals(memberId, chatRoom.getSender().getId()))
            return chatRoom.getReceiver().getId();
        return chatRoom.getSender().getId();
    }

    public ChatRoom findChatRoom(Long chatRoomId) {
        return chatRoomRepository.findChatRoomById(chatRoomId);
    }

    public ChatMessage createMessageByDTO(ChatMessageDTO messageDTO) {
        Member sender = memberService.getMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = findChatRoom(messageDTO.getChatRoomId());

        return new ChatMessage(messageDTO.getContent(), sender, chatRoom);
    }

    @Transactional
    public void saveMessage(ChatMessageDTO messageDTO) {
        ChatMessage chatMessage = createMessageByDTO(messageDTO);
        chatMessageRepository.save(chatMessage);
    }

    // 채팅방 ID로 채팅방 메세지 조회
    public Slice<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {
        return chatRoomRepositorySub.findAllMessagesByChatRoomId(chatRoomId, pageable);
    }

    // 특정 멤버가 속한 모든 채팅방 조회
    public List<ChatRoom> findAllChatRoomByMemberId(Long memberId) {
        return chatRoomRepository.findAllChatRoomsByMemberId(memberId);
    }

    // 상대방 아이디로 해당 채팅방 조회
    public Optional<ChatRoom> findChatRoom(Member loginMember, Member receiver) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomById(loginMember, receiver);
        if (chatRoom.isPresent())
            return chatRoom;

        throw new IllegalArgumentException("둘 사이에 채팅방이 존재하지 않습니다.");
    }

    public Optional<ChatMessage> findLatestChatMessage(Long chatRoomId) {
        List<ChatMessage> messages = chatRoomRepository.findAllMessagesInChatRoom(chatRoomId);
        if (!messages.isEmpty()) {
            messages.sort(Comparator.comparing(ChatMessage::getLocalDateTime).reversed());
            return Optional.of(messages.get(0));
        } else {
            log.info("No chatMessage exists in ChatRoom");
            return Optional.empty();
        }
    }

    public ChatRoomDTO createChatRoomDTO(Long memberId, ChatRoom chatRoom) {
        Long receiverId = chatRoom.getReceiver().getId().equals(memberId) ? chatRoom.getSender().getId() : chatRoom.getReceiver().getId();
        Member member = memberService.getMemberById(receiverId);
        String receiverName = member.getName();

        Optional<ChatMessage> latestChatMessage = findLatestChatMessage(chatRoom.getId());

        LatestMessageDTO latestMessageDTO = latestChatMessage.map(chatMessage ->
                        new LatestMessageDTO(chatMessage.getContent(), chatMessage.getLocalDateTime()))
                .orElse(new LatestMessageDTO(" ", null));

        return new ChatRoomDTO(chatRoom.getId(), receiverId, receiverName, latestMessageDTO,
                member.getMemberImageUrl(),
                chatRoom.getMatching().getBoard().getTitle(),
                chatRoom.getMatching().getDate(),
                chatRoom.getMatching().getStartTime());
    }

    public String save(MultipartFile file) {
        return s3Service.save(file);
    }



}