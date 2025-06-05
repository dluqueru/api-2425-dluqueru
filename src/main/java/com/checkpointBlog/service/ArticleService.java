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
import com.checkpointBlog.model.State;
import com.checkpointBlog.model.User;
import com.checkpointBlog.repository.ArticleCategoryRepository;
import com.checkpointBlog.repository.ArticleRepository;
import com.checkpointBlog.repository.CategoryRepository;
import com.checkpointBlog.repository.UserRepository;
import com.checkpointBlog.service.UserService.ReputationAction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
	public ResponseEntity<?> getArticles(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articlePage = articleRepository.findAll(pageable);
            
            if (articlePage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("articles", articlePage.getContent()
                                    .stream()
                                    .map(ArticleDto::new)
                                    .toList());
            response.put("currentPage", articlePage.getNumber());
            response.put("hasNext", articlePage.hasNext());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en la base de datos");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
	
	// Lista de artículos reportados
	public ResponseEntity<?> getReportedArticles() {
	    try {
	        List<Article> reportedArticles = articleRepository.findByReportedTrue();
	        
	        if (reportedArticles.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        }
	        
	        List<ArticleDto> result = reportedArticles.stream()
	            .map(ArticleDto::new)
	            .collect(Collectors.toList());
	        
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", "Error en la base de datos");
	        errorResponse.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
	
	// Búsqueda de artículos por título
	public ResponseEntity<?> searchArticlesByTitle(String title) {
	    List<Article> list = null;

	    if (title == null || title.trim().isEmpty()) {
	        Map<String, String> response = new HashMap<>();
	        response.put("error", "Parámetro inválido");
	        response.put("message", "El término de búsqueda no puede estar vacío");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	    
	    try {
	        list = articleRepository.findByTitleContainingIgnoreCase(title);
	        
	    } catch (Exception e) {
	        Map<String, String> response = new HashMap<>();
	        response.put("error", "Error en la base de datos");
	        response.put("message", e.getMessage());
	        
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	    }
	    
	    if (list.isEmpty()) {
	        Map<String, String> response = new HashMap<>();
	        response.put("errorMessage", "No se encontraron artículos con: '" + title + "'");
	        
	        return ResponseEntity.status(210).body(response);
	    } else {
	        List<ArticleDto> listResult = list.stream()
	            .map(article -> new ArticleDto(article))
	            .toList();
	        
	        return ResponseEntity.status(HttpStatus.OK).body(listResult);
	    }
	}
	
	// Lista de artículos por usuario en estado borrador
	public ResponseEntity<?> getDraftArticles() {
	    try {
	        String loggedUsername = userService.getLoggedUsername();
	        List<Article> draftArticles = articleRepository.findByStateAndUserUsername(State.DRAFT, loggedUsername);
	        
	        if (draftArticles.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT)
	                .body(Map.of("message", "No tienes artículos en borrador"));
	        }

	        List<ArticleDto> result = draftArticles.stream()
	            .map(ArticleDto::new)
	            .toList();
	        
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", "Error al obtener los borradores");
	        errorResponse.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
		
		// Se incrementan las views cada vez que se accede al artículo si el estado es DEFINITIVE
		if(a.getState().equals(State.DEFINITIVE)) {
			a.incrementViews();
		}
        articleRepository.save(a);
		
		ArticleDto res = new ArticleDto(a);
		
		return ResponseEntity.status(200).body(res);
		
	}
	
	// Añadir un nuevo artículo
	public ResponseEntity<?> addArticle(Article article) {
		// Se verifica que el usuario logueado coincide con el usuario del artículo
	    String loggedUsername = userService.getLoggedUsername();
	    User articleUser = article.getUser();
	    
	    // Se verifica que la reputación del usuario sea suficiente para crear artículo
	    if (!userRepository.findByUsername(loggedUsername).get().canCreateArticle()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Necesitas al menos 5 puntos de reputación para crear artículos"));
	    }

	    // Se verifica coincidencia de usuario O permisos de admin
	    if (!loggedUsername.equals(articleUser.getUsername())) {
	        if (!userService.hasAdminRole()) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("error", "No tienes permisos para esta acción"));
	        }
	    }
		
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
	        
	        // Se incrementa el contador de reputación
	        userService.handleReputationAction(article.getUser().getUsername(), ReputationAction.ARTICLE_CREATE);

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
    
    // Reportar un artículo
    @Transactional
    public ResponseEntity<?> reportArticle(Integer id) {
        try {
            Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
            
            article.setReported(true);
            Article updatedArticle = articleRepository.save(article);
            
            return ResponseEntity.ok(new ArticleDto(updatedArticle));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al reportar el artículo"));
        }
    }

    // Desreportar un artículo
    @Transactional
    public ResponseEntity<?> unreportArticle(Integer id) {
        try {
            Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
            
            article.setReported(false);
            Article updatedArticle = articleRepository.save(article);
            
            return ResponseEntity.ok(new ArticleDto(updatedArticle));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al desreportar el artículo"));
        }
    }
}