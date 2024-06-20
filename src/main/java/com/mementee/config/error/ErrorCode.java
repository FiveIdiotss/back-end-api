package com.mementee.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E4000001", "올바르지 않은 입력값입니다."),
    REQUEST_PARAM_MISSING_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E400002","RequestParam Missing."),
    REQUEST_HEADER_MISSING_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E400002","RequestHeader Missing."),

    //401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401001", "인증 실패"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"E401002", "유효하지 않은 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "E401003", "로그인 실패"),
    REQUIRED_LOGIN(HttpStatus.UNAUTHORIZED, "E401004", "로그인이 필요합니다."),

    //403
    //ex) 자신의 게시물이 아닌데 수정, 삭제할 경우 / 신청에 대해 (멘토, 멘티)에 속하지 않을 경우
    FORBIDDEN(HttpStatus.FORBIDDEN, "E401001", "권한 없음"),

    //404
    NOT_FOUND(HttpStatus.NOT_FOUND, "E404001", "존재하지 않는 엔티티입니다."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "E404002", "존재하지 않는 학과 입니다."),
    SCHOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "E404003", "존재하지 않는 학교 입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404004", "존재하지 않는 회원 입니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "E404005", "존재하지 않는 게시판 입니다."),
    FAVORITE_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "E404006", "즐겨찾기에 존재하지 않는 게시판 입니다."),
    APPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "E404007", "존재하지 않는 신청입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "E404008", "존재하지 않는 신청입니다."),
    LIKE_SUB_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "E404009", "좋아요을 누르지 않았습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E404010", "채팅방이 존재하지 않습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "E404011", "댓글이 존재하지 않습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "E404012", "파일이 존재하지 않습니다."),
    HEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404013", "헤더 정보가 존재하지 않습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "E404014", "해당 알림이 존재하지 않습니다."),

    //405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E2", "잘못된 HTTP 메서드를 호출했습니다."),

    //409
    CONFLICT(HttpStatus.CONFLICT, "E409001", "중복 오류입니다."),
    PROFILE_CONFLICT(HttpStatus.CONFLICT, "E409002", "이미 기본 이미지 입니다."),
    EMAIL_CONFLICT(HttpStatus.CONFLICT, "E409003", "이미 사용중인 이메일입니다."),
    FAVORITE_BOARD_CONFLICT(HttpStatus.CONFLICT, "E409003", "이미 즐겨찾기에 존재하는 게시판 입니다."),
    APPLY_BOARD_CONFLICT(HttpStatus.CONFLICT, "E409004", "이미 신청한 게시판 입니다."),
    MY_APPLY_BOARD_CONFLICT(HttpStatus.CONFLICT, "E409005", "자신의 글에는 신청할 수 없습니다."),
    LIKE_SUB_BOARD_CONFLICT(HttpStatus.CONFLICT, "E409006", "이미 좋아요를 눌렀습니다."),
    EXTEND_REQUEST_CONFLICT(HttpStatus.CONFLICT, "E409007", "이미 연장 신청을 하였습니다."),
    EXTEND_RESPONSE_CONFLICT(HttpStatus.CONFLICT, "E409008", "이미 처리된 신청입니다."),
    MATCHING_CONFLICT(HttpStatus.CONFLICT, "E409007", "이미 완료 된 신청입니다."),

    //413
    MAX_MULTIPART(HttpStatus.REQUEST_ENTITY_TOO_LARGE, "E413001", "파일 사이즈가 너무 큽니다"),

    //415
    UNSUPPORTED_MULTIPART(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "E415001", "지원하지 않는 파일 형식입니다."),

    //500
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500001", "서버 에러가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
