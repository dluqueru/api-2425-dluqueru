package com.checkpointBlog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.ArticleCategory;
import com.checkpointBlog.model.ArticleDto;
import com.checkpointBlog.model.Category;
import com.checkpointBlog.model.User;
import com.checkpointBlog.repository.ArticleCategoryRepository;
import com.checkpointBlog.repository.ArticleRepository;
import com.checkpointBlog.repository.CategoryRepository;
import com.checkpointBlog.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ArticleCategoryRepository articleCategoryRepository;
	
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
	    // Se verifica que el artículo no esté vacío
	    if (article.getTitle() == null || article.getBody() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("El título y el cuerpo son obligatorios");
	    }

	    // Se verifica que las categorías sean válidas
	    if (article.getArticleCategories() == null || article.getArticleCategories().isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("El artículo debe tener al menos una categoría");
	    }

	    // Se buscan las categorías por sus IDs
	    List<Category> categories = categoryRepository.findAllById(
	        article.getArticleCategories().stream()
	              .map(ac -> ac.getCategory().getId())
	              .collect(Collectors.toList())
	    );

	    if (categories.size() != article.getArticleCategories().size()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Algunas categorías no existen");
	    }

	    // Se asignan las categorías encontradas al artículo
	    List<ArticleCategory> articleCategories = new ArrayList<>();
	    for (Category category : categories) {
	        ArticleCategory articleCategory = new ArticleCategory();
	        articleCategory.setArticle(article);
	        articleCategory.setCategory(category);
	        articleCategories.add(articleCategory);
	    }

	    // Se asigna la lista de relaciones artículo-categoría al artículo
	    article.setArticleCategories(articleCategories);

	    // Validación y asignación del User por su username
	    String username = article.getUser().getUsername();
	    Optional<User> userOptional = userRepository.findById(username);
	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Usuario no encontrado con el username: " + username);
	    }

	    // Se asigna el usuario encontrado al artículo
	    article.setUser(userOptional.get());

	    try {
	        // Se guarda el artículo
	        Article savedArticle = articleRepository.save(article);

	        // Se guardan las relaciones artículo-categoría en la tabla intermedia
	        articleCategoryRepository.saveAll(articleCategories);

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(new ArticleDto(savedArticle));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al guardar el artículo");
	    }
	}
    
    // Editar un artículo //TODO NO FUNCIONA
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