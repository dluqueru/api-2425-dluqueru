package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.checkpointBlog.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Optional<Image> findById(Integer id);
    
    List<Image> findImagesByArticleId(Integer articleId);
    
    Optional<Image> findByPublicId(String publicId);
    
    List<Image> findByArticleIdAndFormat(Integer articleId, String format);
    
    @Query("SELECT i FROM Image i WHERE i.article.id = :articleId ORDER BY i.id DESC")
    List<Image> findLatestImagesByArticleId(Integer articleId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.article.id = :articleId")
    void deleteAllByArticleId(Integer articleId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.publicId IS NULL")
    void deleteAllWithoutPublicId();

    boolean existsByPublicId(String publicId);
}