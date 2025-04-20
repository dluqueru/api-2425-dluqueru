package com.checkpointBlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer>{
	
	Optional<Image> findById(Integer id);

	List<Image> findImagesByArticleId(Integer articleId);

}
