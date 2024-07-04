package com.team.mementee.api.dto.notificationDTO;

import lombok.*;

import java.util.Map;

@Builder
@AllArgsConstructor
@Data
public class FcmMessage {
    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Map<String, String> data; // title과 body를 포함하는 데이터 필드
        private String token;
    }
}