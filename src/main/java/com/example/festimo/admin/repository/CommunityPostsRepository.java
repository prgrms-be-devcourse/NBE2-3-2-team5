package com.example.festimo.admin.repository;


import com.example.festimo.admin.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostsRepository extends JpaRepository<Post, Long> {
}
