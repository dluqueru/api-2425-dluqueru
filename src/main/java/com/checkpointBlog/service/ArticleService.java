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
import com.checkpointBlog.repository.ArticleRepository;
import com.checkpointBlog.repository.CategoryRepository;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	// Lista de artículos
	public ResponseEntity<?> getArticles() {
		List<Article> list = null;
		
		try {
			list = articleRepository.findAll();
			
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
			List <ArticleDto> listResult = list.stream().map(article -> new ArticleDto(article)).toList();
			
			return ResponseEntity.status(HttpStatus.OK).body(listResult);
		}
		
	}
	
	// Obtener artículo por id
	public ResponseEntity<?> getArticle (Integer id) {
		Article a;
		
		try {
			a = articleRepository.findById(id).orElse(null);
			
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Error en la base de datos");
			response.put("message", e.getMessage());
			
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
		
		if (a == null) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "El artículo no existe");
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		ArticleDto res = new ArticleDto(a);
		
		return ResponseEntity.status(200).body(res);
		
	}
	
	// Añadir un nuevo artículo
    public ResponseEntity<?> addArticle(Article article) {
    	
        // Se verifica si la categoría existe
        if (article.getCategory() == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Datos inválidos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<Category> categoryOptional = categoryRepository.findById(article.getCategory().getId());
        if (categoryOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "La categoría especificada no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Se asigna la categoría encontrada al artículo
        article.setCategory(categoryOptional.get());

        try {
            Article savedArticle = articleRepository.save(article);
            ArticleDto articleDto = new ArticleDto(savedArticle);
            return ResponseEntity.status(HttpStatus.CREATED).body(articleDto);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al añadir el artículo");
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Editar un artículo
    public ResponseEntity<?> updateArticle(Integer id, Article article) {
        // Se verifica si el artículo existe
        Optional<Article> existingArticleOptional = articleRepository.findById(id);
        if (existingArticleOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "El artículo no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Se verifica si la categoría existe
        if (article.getCategory() == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Datos inválidos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<Category> categoryOptional = categoryRepository.findById(article.getCategory().getId());
        if (categoryOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "La categoría especificada no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Se asigna la categoría encontrada al artículo
        article.setCategory(categoryOptional.get());

        // Se actualiza el artículo existente
        Article existingArticle = existingArticleOptional.get();
        existingArticle.setTitle(article.getTitle());
        existingArticle.setDescription(article.getDescription());
        existingArticle.setCategory(article.getCategory());

        try {
            Article updatedArticle = articleRepository.save(existingArticle);
            ArticleDto articleDto = new ArticleDto(updatedArticle);
            return ResponseEntity.status(HttpStatus.OK).body(articleDto);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar el artículo");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Eliminar un artículo
    public ResponseEntity<?> deleteArticle(Integer id) {
        // Se verifica si el artículo existe
        Optional<Article> existingArticleOpt = articleRepository.findById(id);
        if (existingArticleOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "El artículo no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Article deletedArticle = existingArticleOpt.get();
            articleRepository.deleteById(id);
            ArticleDto articleDto = new ArticleDto(deletedArticle);
            return ResponseEntity.status(HttpStatus.OK).body(articleDto);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el artículo");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
	
}