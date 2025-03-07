package com.checkpointBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.ArticleCategory;
import com.checkpointBlog.model.ArticleCategoryId;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, ArticleCategoryId> {
    
}
