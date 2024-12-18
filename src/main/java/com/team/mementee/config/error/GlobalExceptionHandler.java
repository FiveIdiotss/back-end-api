package com.team.mementee.config.error;

import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.config.error.ErrorCode;
import com.team.mementee.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice   //모든 컨트롤러에서 발생하는 예외를 잡아서 처리
public class GlobalExceptionHandler {

    // 지원하지 않은 HTTP method 호출 할 경우 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // HttpRequestMethodNotSupportedException 예외를 잡아서 처리
    protected ResponseEntity<CommonApiResponse<?>> handle(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    //웹에서 발생하는 예외 처리
    @ExceptionHandler(WebClientResponseException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(WebClientResponseException e) {
        log.error("WebClientResponseException", e);
        return ResponseEntity.status(e.getStatusCode()).body(CommonApiResponse.createError(e.getMessage()));
    }

    //잘못된 값을 입력
    @ExceptionHandler(HttpMessageNotReadableException.class) // HttpRequestMethodNotSupportedException 예외를 잡아서 처리
    protected ResponseEntity<CommonApiResponse<?>> handle(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);
        return createErrorResponseEntity(ErrorCode.BAD_REQUEST);
    }

    //Param, Header 관련 MissingServletRequestParameterException
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        return createErrorResponseEntity(ErrorCode.REQUEST_PARAM_MISSING_BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(MissingRequestHeaderException e) {
        log.error("MissingRequestHeaderException", e);
        return createErrorResponseEntity(ErrorCode.REQUEST_HEADER_MISSING_BAD_REQUEST);
    }

    //MultiPart 관련
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException", e);
        return createErrorResponseEntity(ErrorCode.MAX_MULTIPART);
    }


    @ExceptionHandler(UnsupportedMediaTypeException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(UnsupportedMediaTypeException e) {
        log.error("UnsupportedMediaTypeException", e);
        return createErrorResponseEntity(ErrorCode.UNSUPPORTED_MULTIPART);
    }


    //커스텀한 Exception Handler
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(BaseException e) {
        log.error(e.getErrorCode().getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }


    //BaseException 을 상속받지 않은 Exception 은 이 부분에서 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonApiResponse<?>> handle(Exception e) {
        log.error("Unhandled Exception occurred: {}", e.getMessage(), e); // 예외 메시지와 스택 트레이스 포함
        return createErrorResponseEntity(ErrorCode.SERVER_ERROR);
    }


    private ResponseEntity<CommonApiResponse<?>> createErrorResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(CommonApiResponse.createError(errorCode.getMessage()));
    }
}
