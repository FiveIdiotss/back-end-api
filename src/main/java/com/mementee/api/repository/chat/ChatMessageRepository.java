package com.mementee.api.repository.chat;

import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId ")
    Slice<ChatMessage> findAllMessagesByChatRoomId(@Param("chatRoomId")Long chatRoomId, Pageable pageable);

    //채팅방에 속한 메세지들 조회
    Slice<ChatMessage> findChatMessagesByChatRoom(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findChatMessagesByChatRoom(ChatRoom chatRoom);
}