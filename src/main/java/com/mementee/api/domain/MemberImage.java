package com.mementee.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MemberImage {

    @Id
    @GeneratedValue
    @Column(name = "member_image_id")
    private Long id;

    
}
