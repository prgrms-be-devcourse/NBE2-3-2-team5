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

    INVALID_APPLICATION_STATUS(HttpStatus.BAD_REQUEST, "신청 상태가 유효하지 않습니다."),

    /*
     * 401 UNAUTHORIZED
     */
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다. [로그인] 또는 [회원가입] 후 다시 시도해주세요."),

    /*
     * 403 FORBIDDEN: 권한 없음
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    POST_DELETE_AUTHORIZATION_EXCEPTION(HttpStatus.FORBIDDEN, "작성자 본인 또는 관리자만 게시글을 삭제할 수 있습니다."),
    COMMENT_UPDATE_AUTHORIZATION_EXCEPTION(HttpStatus.FORBIDDEN, "작성자 본인만 댓글을 수정할 수 있습니다."),
    COMMENT_DELETE_AUTHORIZATION_EXCEPTION(HttpStatus.FORBIDDEN, "작성자 본인 또는 관리자만 댓글을 삭제할 수 있습니다."),

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

    COMPANION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 동행의 동행원이 아닙니다."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),


    /*
     * 409 CONFLICT: 사용자의 요청이 서버의 상태와 충돌
     */
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "이미 신청된 사용자입니다."),

    /*
     * 500 INTERNAL_SERVER_ERROR
     */
    IMAGE_UPLOAD_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.")
    ;


    private final HttpStatus status;
    private final String message;
}