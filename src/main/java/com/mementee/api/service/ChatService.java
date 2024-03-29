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

    public void saveImage(String imageCode) throws IOException {
        // 로컬에 저장될 디렉토리 경로
        String directoryPath = "/Users/jonghyunlee/Downloads/savedImage";

        // 이미지 확장자 추출
        String extension = "";
        String[] split = imageCode.split(",");
        String base64Image = split[1];
        if (split[0].equals("data:image/jpeg;base64")) {
            extension = "jpeg";
        } else if (split[0].equals("data:image/png;base64")) {
            extension = "png";
        } else {
            extension = "jpg";
        }

        // Base64 문자열을 바이트 배열로 변환
        byte[] bytes = DatatypeConverter.parseBase64Binary(base64Image);

        // 지정된 디렉토리에 이미지 파일 저장
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리가 존재하지 않으면 생성
        }

        // 파일명 설정
        String fileName = UUID.randomUUID() + "." + extension;

        // 이미지 파일 생성
        File imageFile = new File(directory, fileName);

        // 파일 출력 스트림 초기화
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // 바이트 배열을 파일에 쓰기
            fos.write(bytes);
        }

        // 저장된 파일 경로 출력 (실행 후 확인을 위해 출력)
        System.out.println("이미지가 저장된 경로: " + imageFile.getAbsolutePath());
    }
}