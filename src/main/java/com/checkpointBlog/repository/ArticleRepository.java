package com.checkpointBlog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.Category;

public interface ArticleRepository extends JpaRepository<Article, Integer>{
	
	Optional<Article> findById(Integer id);

}
