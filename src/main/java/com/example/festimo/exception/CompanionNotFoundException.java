package com.example.festimo.exception;

public class CompanionNotFoundException extends CustomException {
  public CompanionNotFoundException() {
    super(ErrorCode.COMPANION_NOT_FOUND);
  }
}
