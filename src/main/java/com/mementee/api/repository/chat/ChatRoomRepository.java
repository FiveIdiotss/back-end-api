package com.mementee.api.repository.chat;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    //채팅방 목록 조회, 멤버 아이디로 조회하도록 수정 필요.
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sender.id = :memberId OR cr.receiver.id = :memberId")
    List<ChatRoom> findChatRoomsByMemberId(@Param("memberId") Long memberId);

    //나와 상대방사이의 채팅방
    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.sender = :loginMember AND cr.receiver = :otherMember) OR " +
            "(cr.sender = :otherMember AND cr.receiver = :loginMember)")
    Optional<ChatRoom> findChatRoomBySenderAndReceiver(@Param("loginMember") Member loginMember, @Param("otherMember") Member otherMember);


}
