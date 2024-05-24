package com.mementee.api.service;

import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.LatestMessageDTO;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.exception.notFound.ChatRoomNotFound;
import com.mementee.s3.S3Service;
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

    @Transactional
    public void markAllMessagesAsRead(Long chatRoomId, Long userId) {
        System.out.println("실행됌");

        chatMessageRepository.markMessageAsRead(chatRoomId, userId);
    }

//
//    public boolean isUserInChatRoom(Long chatRoomId, Long userId) {
//        String key = "chatRoom:" + chatRoomId;
//        return redisTemplate.opsForSet().isMember(key, userId);
//    }
//
//    public Set<Object> getUsersInChatRoom(Long chatRoomId) {
//        String key = "chatRoom:" + chatRoomId;
//        return redisTemplate.opsForSet().members(key);
//    }

    public ChatRoomDTO createChatRoomDTO(Member loginMember, ChatRoom chatRoom) {
        Member receiver = getReceiver(loginMember.getId(), chatRoom);
        LatestMessageDTO latestMessageDTO = LatestMessageDTO.createLatestMessageDTO(findLatestChatMessage(chatRoom.getId()));
        return ChatRoomDTO.createChatRoomDTO(loginMember, receiver, chatRoom, latestMessageDTO);
    }

    public ChatMessage createMessageByDTO(ChatMessageDTO messageDTO) {
        Member sender = memberService.findMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = findChatRoomById(messageDTO.getChatRoomId());
        return new ChatMessage(messageDTO.getFileType(), messageDTO.getFileURL(), messageDTO.getContent(), sender, chatRoom, messageDTO.getLocalDateTime());
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
        if(chatRoom.isEmpty())
            throw new ChatRoomNotFound();
        return chatRoom.get();
    }

    // 상대방 아이디로 해당 채팅방 조회
    public ChatRoom findChatRoomBySenderAndReceiver(Member loginMember, Member receiver) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomBySenderAndReceiver(loginMember, receiver);
        if (chatRoom.isPresent())
            return chatRoom.get();
        throw new ChatRoomNotFound();
    }

    // 채팅방 ID로 채팅방 메세지 조회
    public Slice<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        return chatMessageRepository.findChatMessagesByChatRoom(chatRoom, pageable);
    }

    // 특정 멤버가 속한 모든 채팅방 조회
    public List<ChatRoom> findAllChatRoomByMember(Member member) {
        return chatRoomRepository.findChatRoomsByMember(member);
    }

    @Transactional
    public void saveMessage(ChatMessageDTO messageDTO) {
        ChatMessage chatMessage = createMessageByDTO(messageDTO);
        chatMessageRepository.save(chatMessage);
    }

    public String saveMultipartFile(MultipartFile file) {
        return s3Service.save(file);
    }
}