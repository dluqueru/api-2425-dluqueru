package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.Like;

public interface LikeRepository extends JpaRepository<Like, Integer> {
	
    boolean existsByUserUsernameAndArticleId(String username, Integer articleId);
    
    int countByArticleId(Integer articleId);
    
    Optional<Like> findByUserUsernameAndArticleId(String username, Integer articleId);
    
    @Query("SELECT a FROM Like l JOIN l.article a WHERE l.user.username = :username ORDER BY l.createdAt DESC")
    List<Article> findLikedArticlesByUsername(@Param("username") String username);

	long countByUserUsernameAndArticleId(String username, Integer articleId);
}