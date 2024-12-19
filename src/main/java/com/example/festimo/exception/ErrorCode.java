package com.example.festimo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    INVALID_APPLICATION_STATUS(HttpStatus.BAD_REQUEST, "신청 상태가 유효하지 않습니다."),

    /*
     * 403 FORBIDDEN: 권한 없음
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),


    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보를 찾을 수 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 동행을 찾을 수 없습니다."),

    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 신청을 찾을 수 없습니다."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),

    /*
     * 409 CONFLICT: 사용자의 요청이 서버의 상태와 충돌
     */
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "이미 신청된 사용자입니다."),


    ;

    private final HttpStatus status;
    private final String message;
}
