package com.mementee.api.repository.chat;

import com.mementee.api.domain.chat.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRoomRepositorySub extends JpaRepository<ChatMessage, Long>{

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId ")
    Slice<ChatMessage> findAllMessagesByChatRoomId(@Param("chatRoomId")Long chatRoomId, Pageable pageable);

}