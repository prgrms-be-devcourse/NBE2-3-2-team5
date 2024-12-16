package com.example.festimo.domain.post.entity;

public enum PostCategory {
    NOTICE("공지사항"),
    COMPANION("동행자 모집"),
    REVIEW("후기"),
    QNA("Q&A");

    private final String displayName;

    PostCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
