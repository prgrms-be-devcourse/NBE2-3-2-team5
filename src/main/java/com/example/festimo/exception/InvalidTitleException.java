package com.example.festimo.exception;

public class InvalidTitleException extends CustomException  {
    public InvalidTitleException() {
        super(ErrorCode.INVALID_TITLE);
    }
}
