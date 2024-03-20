package com.mementee.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberImage {

    @Id
    @GeneratedValue
    @Column(name = "member_image_id")
    private Long id;

    //이미지 url
    @Column(name = "member_image_url")
    private String photoUrl;

    public MemberImage(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public MemberImage updateMemberImage(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public MemberImage changeDefaultMemberImage(String defaultPhotoUrl) {
        this.photoUrl = defaultPhotoUrl;
        return this;
    }
}
