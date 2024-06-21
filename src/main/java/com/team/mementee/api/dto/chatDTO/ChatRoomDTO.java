package com.team.mementee.api.dto.chatDTO;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
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
    private int consultTime;                    // 상담 시간

    private int unreadMessageCount;

    private Long boardId;
    private Long matchingId;

    private Long mentorId;

    public static ChatRoomDTO createChatRoomDTO(Member loginMember, Member receiver, ChatRoom chatRoom, LatestMessageDTO latestMessageDTO, int unreadMessageCount,
                                                Long boardId, Long matchingId){
        return new ChatRoomDTO(chatRoom.getId(), receiver.getId(), receiver.getName(), latestMessageDTO,
                loginMember.getMemberImageUrl(),
                chatRoom.getMatching().getBoard().getTitle(),
                chatRoom.getMatching().getDate(),
                chatRoom.getMatching().getStartTime(), chatRoom.getMatching().getConsultTime(),
                unreadMessageCount, boardId, matchingId, chatRoom.getMatching().getMentor().getId());
    }

}
