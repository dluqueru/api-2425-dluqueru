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
	private UserService userService;
	
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
		
		// Se incrementan las views cada vez que se accede al artículo
		a.incrementViews();
        articleRepository.save(a);
		
		ArticleDto res = new ArticleDto(a);
		
		return ResponseEntity.status(200).body(res);
		
	}
	
	// Añadir un nuevo artículo
	public ResponseEntity<?> addArticle(Article article) {
	    // Se verifica que el artículo no esté vacío
	    if (article.getTitle() == null || article.getBody() == null) {
	    	Map<String, String> response = new HashMap<>();
			response.put("error", "El título y el cuerpo son obligatorios");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(response);
	    }

	    // Se verifica que las categorías sean válidas
	    if (article.getArticleCategories() == null || article.getArticleCategories().isEmpty()) {
	    	Map<String, String> response = new HashMap<>();
			response.put("error", "El artículo debe tener al menos una categoría");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(response);
	    }

	    // Se buscan las categorías por sus IDs
	    List<Category> categories = categoryRepository.findAllById(
	        article.getArticleCategories().stream()
	              .map(ac -> ac.getCategory().getId())
	              .collect(Collectors.toList())
	    );

	    if (categories.size() != article.getArticleCategories().size()) {
	    	Map<String, String> response = new HashMap<>();
			response.put("error", "Algunas categorías no existen");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(response);
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
	    	Map<String, String> response = new HashMap<>();
			response.put("error", "Usuario no encontrado con el username " + username);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(response);
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
	    	Map<String, String> response = new HashMap<>();
			response.put("error", "Error añadiendo el artículo");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(response);
	    }
	}
    
    // Editar un artículo
	@Transactional
	public ResponseEntity<?> updateArticle(Integer articleId, ArticleDto articleDto) {
	    try {
	        // Validaciones
	        if (articleId == null) {
	            throw new IllegalArgumentException("El ID del artículo no puede ser nulo");
	        }
	        if (articleDto.getUsername() == null) {
	            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo");
	        }
	        if (articleDto.getCategories() == null) {
	            throw new IllegalArgumentException("La lista de categorías no puede ser nula");
	        }

	        // Se recupera el artículo existente
	        Article article = articleRepository.findById(articleId)
	                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
	        
	        // Se verifica si el usuario que está borrando tiene el rol de ADMIN o es el que creó el artículo
	        if (!userService.getLoggedUsername().equals(article.getUser().getUsername()) && !userService.hasAdminRole()) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("error", "Sólo un admin o el mismo usuario puede actualizar este artículo"));
	        }

	        // Se actualizan las propiedades del artículo
	        article.setTitle(articleDto.getTitle());
	        article.setBody(articleDto.getBody());
	        article.setReported(articleDto.isReported());
	        article.setState(articleDto.getState());
	        article.setPublishDate(articleDto.getPublishDate());
	        article.setViews(articleDto.getViews());

	        // Se actualiza el usuario
	        User user = userRepository.findById(articleDto.getUsername())
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	        article.setUser(user);

	        // Se obtienen las categorías actuales del artículo
	        List<ArticleCategory> currentCategories = new ArrayList<>(article.getArticleCategories());

	        // Se mapean las categorías del DTO
	        List<ArticleCategory> newCategories = articleDto.getCategories().stream()
	                .map(articleCategoryDto -> {
	                    Integer categoryId = articleCategoryDto.getCategoryId();
	                    Category category = categoryRepository.findById(categoryId)
	                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
	                    ArticleCategory articleCategory = new ArticleCategory();
	                    articleCategory.setArticle(article);
	                    articleCategory.setCategory(category);
	                    return articleCategory;
	                })
	                .collect(Collectors.toList());

	        // Se eliminan las categorías que ya no están en el DTO
	        for (ArticleCategory currentCategory : currentCategories) {
	            if (newCategories.stream().noneMatch(newCategory -> newCategory.getCategory().equals(currentCategory.getCategory()))) {
	                article.getArticleCategories().remove(currentCategory);
	            }
	        }

	        // Se agregan las nuevas categorías
	        for (ArticleCategory newCategory : newCategories) {
	            if (currentCategories.stream().noneMatch(currentCategory -> currentCategory.getCategory().equals(newCategory.getCategory()))) {
	                article.getArticleCategories().add(newCategory);
	            }
	        }
	        
	        // Se guarda el artículo actualizado
	        Article updatedArticle = articleRepository.save(article);

	        // Devuelve respuesta exitosa
	        return ResponseEntity.status(HttpStatus.OK).body(new ArticleDto(updatedArticle));

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
	    }
	}
	
    // Eliminar un artículo
    @Transactional
    public ResponseEntity<?> deleteArticle(Integer id) {
        Optional<Article> existingArticleOpt = articleRepository.findById(id);
        
    	// Se verifica si el usuario que está borrando tiene el rol de ADMIN o es el que creó el artículo
        if (!userService.getLoggedUsername().equals(existingArticleOpt.get().getUser().getUsername()) && !userService.hasAdminRole()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Sólo un admin o el mismo usuario puede borrar este artículo"));
        }
        
        if (existingArticleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "El artículo no existe"));
        }

        try {
            Article articleToDelete = existingArticleOpt.get();
            ArticleDto articleDto = new ArticleDto(articleToDelete);

            // Se limpia la lista de categorías
            articleToDelete.getArticleCategories().clear();
            articleRepository.save(articleToDelete);

            // Se elimina el artículo
            articleRepository.delete(articleToDelete);
            
            return ResponseEntity.status(HttpStatus.OK).body(articleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error al eliminar el artículo",
                "message", e.getMessage()
            ));
        }
    }
}