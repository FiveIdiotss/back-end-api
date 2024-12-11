package com.team.mementee.api.domain.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface EsBoardRepository extends ElasticsearchRepository<EsBoard, String> {

    List<EsBoard> findAllByTitle(String keyWord);

    List<EsBoard> findAllByContent(String keyWord);

    EsBoard findByBoardId(Long boardId);

}

