package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id @GeneratedValue
    @Column(name = "match_id")
    private Long id;

//    //멘토는 1명 멘티는 여러명
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member mentor;


}
