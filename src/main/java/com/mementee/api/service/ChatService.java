package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.repository.chat.ChatRoomRepositorySub;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomRepositorySub chatRoomRepositorySub;

    public ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findChatRoomById(chatRoomId);
    }

    public ChatMessage createMessage(String content, Member member, ChatRoom chatRoom) {
        return new ChatMessage(content, member, chatRoom);
    }

    @Transactional
    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public void saveChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }


    public void assignMessageToChatRoom(Long messageId, Long chatRoomId) {
        // 메시지와 채팅방 가져오기
        ChatMessage chatMessage = chatMessageRepository.findMessageById(messageId);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(chatRoomId);

        // 채팅 메시지를 채팅방에 할당
        chatMessage.setChatRoom(chatRoom);
        chatMessageRepository.save(chatMessage);
    }

    // 센더, 리시버로 찾기, 멤버는 id값으로 등록되어 있음.
    // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
    @Transactional
    public ChatRoom findOrCreateChatRoom(Member sender, Member receiver) {
        Optional<Long> bySendAndReceiver = chatRoomRepository.findChatRoomBySenderAndReceiver(sender.getId(), receiver.getId());

        if (bySendAndReceiver.isEmpty()) {
            System.out.println("create a new chatroom");
            ChatRoom newChatroom = new ChatRoom(sender, receiver);
            this.saveChatRoom(newChatroom);
            return newChatroom;
        }

        System.out.println("use exist chatroom");
        return chatRoomRepository.findChatRoomById(bySendAndReceiver.get());
    }

    // 채팅방 ID로 채팅방 메세지 조회
    public Slice<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId, Pageable pageable){
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(chatRoomId);
        return chatRoomRepositorySub.findAllMessagesByChatRoomId(chatRoomId, pageable);
    }

    // 특정 멤버가 속한 모든 채팅방 조회
    public List<ChatRoom> findAllChatRoomByMember(Member member) {
        Long id = member.getId();
        return chatRoomRepository.findAllChatRoomsByMemberId(id);
    }

    // 상대방 아이디로 해당 채팅방 조회
    public ChatRoom findChatRoomOrCreate(Member loginMember, Member receiver) {
        return chatRoomRepository.findOrCreateChatRoomById(loginMember, receiver);
    }

    public ChatMessage findLatestChatMessage(Long chatRoomId) {
        List<ChatMessage> messages = chatRoomRepository.findAllMessagesInChatRoom(chatRoomId);
        if (!messages.isEmpty()) {
            messages.sort(Comparator.comparing(ChatMessage::getLocalDateTime).reversed());
            return messages.get(0);
        } else {
            throw new NoSuchElementException("No chat message exits");
        }
    }

}