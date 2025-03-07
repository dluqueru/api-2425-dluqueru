package com.checkpointBlog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.ArticleDto;
import com.checkpointBlog.model.Category;
import com.checkpointBlog.model.CategoryDto;
import com.checkpointBlog.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	// Lista de categorías
	public ResponseEntity<?> getCategories() {
		List<Category> list = null;
		
		try {
			list = categoryRepository.findAll();
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Error en la base de datos");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
		
		if (list.isEmpty()) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "Lista vacía");
			return ResponseEntity.status(210).body(response);
		} else {
			List <CategoryDto> listResult = list.stream().map(category -> new CategoryDto(category)).toList();
			return ResponseEntity.status(HttpStatus.OK).body(listResult);
		}
		
	}

	// Obtener categoría por su id
	public ResponseEntity<?> getCategory (Integer id) {
		Category c = null;
		
		try {
			c = categoryRepository.findById(id).orElse(null);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Error en la base de datos");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
		
		if (c == null) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "La categoría no existe");
			return ResponseEntity.status(404).body(response);
		}
		
		CategoryDto categoryDto = new CategoryDto(c);
		return ResponseEntity.status(200).body(categoryDto);
		
	}
	
	// Añadir una categoría
    public ResponseEntity<?> addCategory(Category category) {
        try {
            Category savedCategory = categoryRepository.save(category);
            CategoryDto categoryDto = new CategoryDto(savedCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al añadir el artículo");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Editar una categoría
    public ResponseEntity<?> updateCategory(Integer id, Category category) {
        
    	Optional<Category> existingCategoryOptional = categoryRepository.findById(id);
        
        // Se verifica si la categoría existe
        if (existingCategoryOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "La categoría no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Se actualiza la categoría existente
        Category existingCategory = existingCategoryOptional.get();
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setArticleList(category.getArticleList());

        try {
        	Category updatedCategory = categoryRepository.save(existingCategory);
        	CategoryDto categoryDto = new CategoryDto(updatedCategory);
            return ResponseEntity.status(HttpStatus.OK).body(categoryDto);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar la categoría");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

 // Eliminar una categoría
    @Transactional
    public ResponseEntity<?> deleteCategory(Integer id) {
    	
    	Optional<Category> existingCategoryOpt = categoryRepository.findById(id);
        
    	// Se verifica si la categoría existe
        if (existingCategoryOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "La categoría no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Category deletedCategory = existingCategoryOpt.get();
            categoryRepository.deleteById(id);
            CategoryDto categoryDto = new CategoryDto(deletedCategory);
            return ResponseEntity.status(HttpStatus.OK).body(categoryDto);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar la categoría");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
