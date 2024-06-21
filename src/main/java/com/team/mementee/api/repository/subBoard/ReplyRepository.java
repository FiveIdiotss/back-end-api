package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.Reply;
import com.team.mementee.api.domain.SubBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository <Reply, Long> {

    Page<Reply> findRepliesBySubBoard(SubBoard subBoard, Pageable pageable);
}
