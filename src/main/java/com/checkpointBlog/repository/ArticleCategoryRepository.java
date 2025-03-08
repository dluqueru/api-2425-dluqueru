package com.checkpointBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkpointBlog.model.ArticleCategory;
import com.checkpointBlog.model.ArticleCategoryId;


public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, ArticleCategoryId> {
	@Modifying
    @Query("DELETE FROM ArticleCategory ac WHERE ac.article.id = :articleId")
    void deleteByArticleId(@Param("articleId") Integer articleId);
}
