package com.mementee.api.repository.subBoard;

import com.mementee.api.domain.Reply;
import com.mementee.api.domain.SubBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository <Reply, Long> {

    Page<Reply> findRepliesBySubBoard(SubBoard subBoard, Pageable pageable);
}
