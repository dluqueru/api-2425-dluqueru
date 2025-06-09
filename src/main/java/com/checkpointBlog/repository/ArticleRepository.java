package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.State;

public interface ArticleRepository extends JpaRepository<Article, Integer>{
	
	Optional<Article> findById(Integer id);

	List<Article> findByTitleContainingIgnoreCase(String title);
	
	List<Article> findByReportedTrue();
	
	List<Article> findByStateAndUserUsername(State state, String username);
	
    @Query("SELECT a FROM Article a JOIN a.user u WHERE a.state = 'DEFINITIVE' ORDER BY u.reputation DESC, a.publishDate DESC")
    List<Article> findPublishedOrderByUserReputationDesc();
    
    @Query("SELECT a FROM Article a JOIN a.user u WHERE a.state = 'DEFINITIVE' ORDER BY u.reputation DESC, a.publishDate DESC")
    Page<Article> findPublishedOrderByUserReputationDesc(Pageable pageable);
    
    ///////////////////
    @Query("SELECT a FROM Article a " +
	       "JOIN ArticleCategory ac ON a.id = ac.article.id " +
	       "WHERE ac.category.id = :categoryId AND a.state = 'DEFINITIVE' " +
	       "ORDER BY a.publishDate DESC")
	List<Article> findDefinitiveByCategoryOrderByPublishDateDesc(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT a FROM Article a " +
 	       "JOIN ArticleCategory ac ON a.id = ac.article.id " +
 	       "WHERE ac.category.id = :categoryId AND a.state = 'DEFINITIVE' " +
 	       "ORDER BY a.publishDate ASC")
 	List<Article> findDefinitiveByCategoryOrderByPublishDateAsc(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT a FROM Article a " +
 	       "JOIN ArticleCategory ac ON a.id = ac.article.id " +
 	       "WHERE ac.category.id = :categoryId AND a.state = 'DEFINITIVE' " +
 	       "ORDER BY a.views DESC")
 	List<Article> findDefinitiveByCategoryOrderByViewsDesc(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT a FROM Article a " +
  	       "JOIN ArticleCategory ac ON a.id = ac.article.id " +
  	       "WHERE ac.category.id = :categoryId AND a.state = 'DEFINITIVE' " +
  	       "ORDER BY a.views ASC")
  	List<Article> findDefinitiveByCategoryOrderByViewsAsc(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT a FROM Article a WHERE a.state = 'DEFINITIVE' ORDER BY a.publishDate DESC")
    List<Article> findDefinitiveOrderByPublishDateDesc();
    
    @Query("SELECT a FROM Article a WHERE a.state = 'DEFINITIVE' ORDER BY a.publishDate ASC")
    List<Article> findDefinitiveOrderByPublishDateAsc();

    @Query("SELECT a FROM Article a WHERE a.state = 'DEFINITIVE' ORDER BY a.views DESC")
    List<Article> findDefinitiveOrderByViewsDesc();
    
    @Query("SELECT a FROM Article a WHERE a.state = 'DEFINITIVE' ORDER BY a.views ASC")
    List<Article> findDefinitiveOrderByViewsAsc();
}
