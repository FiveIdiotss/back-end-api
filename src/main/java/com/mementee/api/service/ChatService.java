package com.mementee.api.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.LatestMessageDTO;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.repository.chat.ChatRoomRepositorySub;
import com.mementee.s3.S3Config;
import com.mementee.s3.S3Service;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findChatRoomById(chatRoomId);
    }

    public ChatMessage createMessageByDTO(ChatMessageDTO messageDTO) {
        Member sender = memberService.getMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = findChatRoom(messageDTO.getChatRoomId());

        return new ChatMessage(messageDTO.getContent(), sender, chatRoom, messageDTO.getImage());
    }

    @Transactional
    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    // 채팅방 ID로 채팅방 메세지 조회
    public Slice<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId, Pageable pageable){
        return chatRoomRepositorySub.findAllMessagesByChatRoomId(chatRoomId, pageable);
    }

    // 특정 멤버가 속한 모든 채팅방 조회
    public List<ChatRoom> findAllChatRoomByMemberId(Long memberId) {
        return chatRoomRepository.findAllChatRoomsByMemberId(memberId);
    }

    // 상대방 아이디로 해당 채팅방 조회
    public Optional<ChatRoom> findChatRoom(Member loginMember, Member receiver) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomById(loginMember, receiver);
        if(chatRoom.isPresent())
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
        boolean hasImage = latestChatMessage.map(ChatMessage::getImage).isPresent();

        LatestMessageDTO latestMessageDTO = latestChatMessage.map(chatMessage ->
                        new LatestMessageDTO(chatMessage.getContent(), chatMessage.getLocalDateTime(), hasImage))
                .orElse(new LatestMessageDTO(" ", null, false));

        return new ChatRoomDTO(chatRoom.getId(), receiverId, receiverName, latestMessageDTO,
                member.getMemberImage().getMemberImageUrl(),
                chatRoom.getMatching().getBoard().getTitle(),
                chatRoom.getMatching().getDate(),
                chatRoom.getMatching().getStartTime());
    }

    public String saveImage(String imageCode) {
        log.info("s3 저장 로직");
        return s3Service.saveChatImage(imageCode);
    }
}