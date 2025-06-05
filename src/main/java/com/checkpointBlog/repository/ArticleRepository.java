package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.State;

public interface ArticleRepository extends JpaRepository<Article, Integer>{
	
	Optional<Article> findById(Integer id);

	List<Article> findByTitleContainingIgnoreCase(String title);
	
	List<Article> findByReportedTrue();
	
	List<Article> findByStateAndUserUsername(State state, String username);
	
    @Query("SELECT a FROM Article a JOIN a.user u WHERE a.state = 'DEFINITIVE' ORDER BY u.reputation DESC, a.publishDate DESC")
    List<Article> findAllOrderByUserReputationDesc();
    
    @Query("SELECT a FROM Article a JOIN a.user u WHERE a.state = 'DEFINITIVE' ORDER BY u.reputation DESC, a.publishDate DESC")
    Page<Article> findPublishedOrderByUserReputationDesc(Pageable pageable);
}
