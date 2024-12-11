//package com.team.mementee.api.domain.document;
//
//import com.team.mementee.api.domain.Board;
//import lombok.*;
//import org.springframework.data.elasticsearch.annotations.*;
//import org.springframework.data.annotation.Id;
//
//@Document(indexName = "board")
//@AllArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//@Builder
//public class EsBoard {
//
//    @Id
//    private String id;
//
//    @Field(type = FieldType.Long)
//    private Long boardId;
//
//    @Field(type = FieldType.Text)
//    private String title;
//
//    @Field(type = FieldType.Text)
//    private String content;
//
//    @Field(type = FieldType.Keyword)
//    private String boardCategory;
//
//    @Field(type = FieldType.Long)
//    private Long memberId;
//
//    public static EsBoard toDocument(Board board) {
//        return EsBoard.builder()
//                .boardId(board.getId())
//                .title(board.getTitle())
//                .content(board.getContent())
//                .boardCategory(board.getBoardCategory().name())
//                .memberId(board.getMember().getId())
//                .build();
//    }
//
//    public static EsBoard updatedEsBoard(String id, Board board) {
//        return EsBoard.builder()
//                .id(id)
//                .boardId(board.getId())
//                .title(board.getTitle())
//                .content(board.getContent())
//                .boardCategory(board.getBoardCategory().name())
//                .memberId(board.getMember().getId())
//                .build();
//    }
//}