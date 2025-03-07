package com.checkpointBlog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.Category;
import com.checkpointBlog.model.CategoryDto;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	Optional<Category> findById(CategoryDto categoryDto);

}
