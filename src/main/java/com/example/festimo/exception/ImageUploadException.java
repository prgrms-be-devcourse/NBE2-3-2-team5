package com.example.festimo.exception;

public class ImageUploadException extends CustomException {
    public ImageUploadException() {
        super(ErrorCode.IMAGE_UPLOAD_EXCEPTION);
    }
}
