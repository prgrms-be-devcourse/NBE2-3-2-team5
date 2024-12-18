package com.example.festimo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /*
     * 204 NO CONTENT
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "조회된 데이터가 없습니다."),

    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상, 페이지 크기는 1 이상이어야 합니다."),
    INVALID_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST, "비밀번호를 다시 확인해주새요."),

    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}