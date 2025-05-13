package com.checkpointBlog.controller;

import com.checkpointBlog.model.LikeDto;
import com.checkpointBlog.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Integer articleId,
            Authentication authentication) {
        
        String username = authentication.getName();
        LikeDto likeDto = likeService.toggleLike(username, articleId);
        int likeCount = likeService.getLikeCount(articleId);
        boolean hasLiked = likeDto != null;

        Map<String, Object> response = new HashMap<>();
        response.put("liked", hasLiked);
        response.put("likeCount", likeCount);
        
        if (hasLiked) {
            response.put("like", likeDto);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/{articleId}")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Integer articleId) {
        return ResponseEntity.ok(likeService.getLikeCount(articleId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUserLike(
            @RequestParam Integer articleId,
            Authentication authentication) {
        
        String username = authentication.getName();
        return ResponseEntity.ok(likeService.hasUserLiked(username, articleId));
    }
}