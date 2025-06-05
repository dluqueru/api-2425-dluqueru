package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.State;

public interface ArticleRepository extends JpaRepository<Article, Integer>{
	
	Optional<Article> findById(Integer id);

	List<Article> findByTitleContainingIgnoreCase(String title);
	
	List<Article> findByReportedTrue();
	
	List<Article> findByStateAndUserUsername(State state, String username);
}
