package com.checkpointBlog.service;

import com.checkpointBlog.model.Like;
import com.checkpointBlog.model.LikeDto;
import com.checkpointBlog.model.User;
import com.checkpointBlog.model.Article;
import com.checkpointBlog.repository.LikeRepository;
import com.checkpointBlog.service.UserService.ReputationAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    
    @Autowired
	private UserService userService;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional
    public LikeDto toggleLike(String username, Integer articleId) {
        Optional<Like> existingLike = likeRepository.findByUserUsernameAndArticleId(username, articleId);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            
            // Se decrementa el contador de reputación
	        userService.handleReputationAction(username, ReputationAction.ARTICLE_DISLIKE);
            return null;
        } else {
            User user = new User();
            user.setUsername(username);
            
            Article article = new Article();
            article.setId(articleId);
            
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setArticle(article);
            newLike.setCreatedAt(LocalDateTime.now());
            
            Like savedLike = likeRepository.save(newLike);
            
            // Se incrementa el contador de reputación
	        userService.handleReputationAction(username, ReputationAction.ARTICLE_LIKE);
	        
            return convertToDto(savedLike);
        }
    }

    public int getLikeCount(Integer articleId) {
        return likeRepository.countByArticleId(articleId.intValue());
    }

    public boolean hasUserLiked(String username, Integer articleId) {
        return likeRepository.existsByUserUsernameAndArticleId(username, articleId);
    }

    private LikeDto convertToDto(Like like) {
        return new LikeDto(like);
    }
}