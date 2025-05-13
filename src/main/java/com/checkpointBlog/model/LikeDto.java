package com.checkpointBlog.model;

import java.time.LocalDateTime;

public class LikeDto {
	 private Integer id;
	 private String username;
	 private Integer articleId;
	 private LocalDateTime createdAt;
	
	 public LikeDto() {}
	
	 public LikeDto(Like like) {
	     this.id = like.getId();
	     this.username = like.getUser().getUsername();
	     this.articleId = like.getArticle().getId();
	     this.createdAt = like.getCreatedAt();
	 }
	
	 public Integer getId() {
	     return id;
	 }
	
	 public void setId(Integer id) {
	     this.id = id;
	 }
	
	 public String getUsername() {
	     return username;
	 }
	
	 public void setUsername(String username) {
	     this.username = username;
	 }
	
	 public Integer getArticleId() {
	     return articleId;
	 }
	
	 public void setArticleId(Integer articleId) {
	     this.articleId = articleId;
	 }
	
	 public LocalDateTime getCreatedAt() {
	     return createdAt;
	 }
	
	 public void setCreatedAt(LocalDateTime createdAt) {
	     this.createdAt = createdAt;
	 }
}