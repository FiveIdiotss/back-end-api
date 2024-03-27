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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomRepositorySub chatRoomRepositorySub;
    private final MemberService memberService;

    public ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findChatRoomById(chatRoomId);
    }

    public ChatMessage createMessageByDTO(ChatMessageDTO messageDTO) {
        Member sender = memberService.getMemberById(messageDTO.getSenderId());
        ChatRoom chatRoom = findChatRoom(messageDTO.getChatRoomId());

        return new ChatMessage(messageDTO.getContent(), sender, chatRoom);
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
    public ChatRoom findChatRoomOrCreate(Member loginMember, Member receiver) {
        return chatRoomRepository.findOrCreateChatRoomById(loginMember, receiver);
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
        String receiverName = chatRoom.getReceiver().getId().equals(memberId) ? chatRoom.getSender().getName() : chatRoom.getReceiver().getName();

        Optional<ChatMessage> latestChatMessage = findLatestChatMessage(chatRoom.getId());
        LatestMessageDTO latestMessageDTO = new LatestMessageDTO(latestChatMessage.get().getContent(), latestChatMessage.get().getLocalDateTime());

        return new ChatRoomDTO(chatRoom.getId(), receiverId, receiverName, latestMessageDTO);
    }

    public void test(MultipartFile mefilessage) throws IOException {
        String directoryPath = "/User/jonghyunlee/Downloads";

        File directory = new File(directoryPath);

        String filePath =  directoryPath + "/" + mefilessage.getOriginalFilename();
        File dest = new File(filePath);
        mefilessage.transferTo(dest);

        log.info("파일이 저장되었습니다.");

//        String extenstion;
//        String[] split = mefilessage.split(",");
//        if (split[0].equals("data:image/jpeg;base64")) extenstion = "jpeg";
//        else if (split[0].equals("data:image/png;base64")) extenstion = "png";
//        else extenstion = "jpg";
//
//        byte[] bytes = DatatypeConverter.parseBase64Binary(split[1]);
//        FileOutputStream fos = new FileOutputStream(directoryPath);
//        fos.write(bytes);
    }
}