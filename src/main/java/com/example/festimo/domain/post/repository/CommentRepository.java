package com.example.festimo.domain.post.repository;

import com.example.festimo.domain.post.entity.Comment;
import com.example.festimo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COALESCE(MAX(c.sequence), 0) FROM Comment c WHERE c.post = :post")
    Integer findMaxSequenceByPost(@Param("post") Post post);
}