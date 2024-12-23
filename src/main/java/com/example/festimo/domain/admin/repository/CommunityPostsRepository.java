package com.example.festimo.domain.admin.repository;


import com.example.festimo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostsRepository extends JpaRepository<Post, Long> {
}
