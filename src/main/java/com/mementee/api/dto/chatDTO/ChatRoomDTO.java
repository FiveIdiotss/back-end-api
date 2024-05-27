package com.mementee.api.dto.chatDTO;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long chatRoomId;
    private Long receiverId;
    private String receiverName;
    private LatestMessageDTO latestMessageDTO;

    private String receiverImageUrl;

    private String boardTitle;
    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간

    private int unreadMessageCount;

    public ChatRoomDTO(Long chatRoomId, Long receiverId, String receiverName) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }

    public static ChatRoomDTO createChatRoomDTO(Member loginMember, Member receiver, ChatRoom chatRoom, LatestMessageDTO latestMessageDTO, int unreadMessageCount){
        return new ChatRoomDTO(chatRoom.getId(), receiver.getId(), receiver.getName(), latestMessageDTO,
                loginMember.getMemberImageUrl(),
                chatRoom.getMatching().getBoard().getTitle(),
                chatRoom.getMatching().getDate(),
                chatRoom.getMatching().getStartTime(),
                unreadMessageCount);
    }


}
