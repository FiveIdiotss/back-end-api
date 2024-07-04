package com.team.mementee.social;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialMember {
    private String id;
    private String name;
    private String email;
    private String gender;
    private String mobile;
    private String nickname;
    private SocialLoginType socialLoginType;
}
