package com.mementee.api.repository.chat;

import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId ")
    Slice<ChatMessage> findAllMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    //채팅방에 속한 메세지들 조회
    Slice<ChatMessage> findChatMessagesByChatRoom(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findChatMessagesByChatRoom(ChatRoom chatRoom);

    //해당 채팅방 내에 상대방이 보낸 메시지 읽을 처리.
    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage cm SET cm.readCount = 2 WHERE cm.chatRoom.id = :chatRoomId AND cm.sender.id != :userId AND cm.readCount = 1")
    void markMessageAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    // 채팅방 내에에 본인이 읽지 않은 메시지 개수 반환.
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.sender.id <> :memberId AND m.readCount = 1")
    int getUnreadMessageCount(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);
}