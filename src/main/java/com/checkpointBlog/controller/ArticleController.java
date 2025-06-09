package com.checkpointBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkpointBlog.exception.ElementNotFoundException;
import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.ArticleDto;
import com.checkpointBlog.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Artículos", description = "Operaciones relacionadas con artículos")
public class ArticleController {

	@Autowired
	private ArticleService articleService;
	
	// Lista de artículos ordenados por la reputación de su autor y por fecha de creación
	@GetMapping("/article")
    @Operation(summary = "Obtener artículos paginados", 
              description = "Devuelve una lista paginada de artículos (4 por página)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Artículos encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay más artículos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> listArticles(
            @Parameter(description = "Número de página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de la página", example = "4")
            @RequestParam(defaultValue = "4") int size) {
        
        return articleService.getArticles(page, size);
    }
	
	// Lista de artículos reportados
	@GetMapping("/article/reported")
	@Operation(summary = "Obtener artículos reportados", 
	          description = "Devuelve todos los artículos reportados (requiere rol ADMIN)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Artículos reportados encontrados"),
	    @ApiResponse(responseCode = "204", description = "No hay artículos reportados"),
	    @ApiResponse(responseCode = "403", description = "No tienes permisos para esta acción"),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<?> getReportedArticles() {
	    return articleService.getReportedArticles();
	}

	// Lista de artículos por categoría y orden
	@GetMapping("/article/category/{categoryId}")
	@Operation(
	    summary = "Obtener artículos por categoría y orden",
	    description = "Devuelve artículos DEFINITIVE filtrados por categoría, ordenados por fecha o vistas"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Artículos encontrados"),
	    @ApiResponse(responseCode = "204", description = "No hay artículos"),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<?> getArticlesByCategoryAndSort(
	    @Parameter(description = "ID de la categoría", required = true)
	    @PathVariable Integer categoryId,
	    
	    @Parameter(description = "Campo para ordenar (date|views)", example = "date")
	    @RequestParam(defaultValue = "date") String sortBy,
	    
	    @Parameter(description = "Dirección de orden (asc|desc)", example = "desc")
	    @RequestParam(defaultValue = "desc") String sortDirection) {
	    
	    return articleService.getDefinitiveArticlesByCategoryAndSort(
	        categoryId, sortBy, sortDirection
	    );
	}

	// Lista de artículos ordenados
	@GetMapping("/article/sorted")
	@Operation(
	    summary = "Obtener artículos ordenados",
	    description = "Devuelve artículos DEFINITIVE ordenados por fecha o vistas"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Artículos encontrados"),
	    @ApiResponse(responseCode = "204", description = "No hay artículos"),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<?> getArticlesSorted(
	    @Parameter(description = "Campo para ordenar (date|views)", example = "date")
	    @RequestParam(defaultValue = "date") String sortBy,
	    
	    @Parameter(description = "Dirección de orden (asc|desc)", example = "desc")
	    @RequestParam(defaultValue = "desc") String sortDirection) {
	    
	    return articleService.getDefinitiveArticlesSorted(sortBy, sortDirection);
	}
	
	// Obtener artículo por id
	@GetMapping("/article/{id}")
	@Operation(summary = "Obtener artículo por ID", description = "Devuelve un artículo basado en su ID")
	@ApiResponses({
		 @ApiResponse(responseCode = "200", description = "Artículo encontrado"),
		 @ApiResponse(responseCode = "404", description = "Artículo no encontrado"),
		 @ApiResponse(responseCode = "500", description = "Error interno del servidor")
		})
	public ResponseEntity<?> getArticle(
			@Parameter(description = "ID del artículo", example = "1", required = true)
			@PathVariable Integer id) {
		ResponseEntity<?> a = articleService.getArticle(id);
		
		if(a == null) {
			throw new ElementNotFoundException(id);
		} else {
			return a;
		}
	}
	
	// Búsqueda de artículos
	@GetMapping("/article/search")
	@Operation(
	    summary = "Buscar artículos por título", 
	    description = "Devuelve una lista de artículos cuyo título contiene el texto buscado (no sensible a mayúsculas/minúsculas)"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito"),
	    @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda vacío"),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<?> searchArticlesByTitle(
	    @Parameter(
	        description = "Texto a buscar en los títulos de los artículos", 
	        example = "zelda", 
	        required = true
	    )
	    @RequestParam String title) {
	    
	    return articleService.searchArticlesByTitle(title);
	}
	
	// Artículos en estado borrador del usuario logueado
	@GetMapping("/article/drafts")
	@Operation(
	    summary = "Obtener artículos en borrador del usuario logueado",
	    description = "Devuelve una lista de artículos en estado DRAFT pertenecientes al usuario actual"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Borradores encontrados"),
	    @ApiResponse(responseCode = "204", description = "No hay borradores"),
	    @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<?> getDraftArticles() {
	    return articleService.getDraftArticles();
	}
	
	// Añadir un artículo
//	FORMATO PARA AÑADIR
//	{
//	  "title": "La importancia de la narrativa en los videojuegos",
//	  "body": "En este artículo analizamos cómo la narrativa en los videojuegos ha evolucionado y su impacto en la experiencia del jugador.",
//	  "reported": false,
//	  "state": "DRAFT",
//	  "publishDate": null,
//	  "views": 0,
//	  "user": {
//	    "username": "user1"
//	  },
//	  "articleCategories": [
//	    {
//	      "category": {
//	        "id": 1
//	      }
//	    },
//	    {
//	      "category": {
//	        "id": 2
//	      }
//	    }
//	  ]
//	}
    @PostMapping("/article")
    public ResponseEntity<?> addArticle(@RequestBody Article article) {
        return articleService.addArticle(article);
    }
    
    // Editar un artículo
//	FORMATO PARA EDITAR
//    {
//	  "id": 20,
//	  "title": "Vamos allá",
//	  "body": "En este artículo analizamos cómo la narrativa en los videojuegos ha evolucionado y su impacto en la experiencia del jugador.",
//	  "reported": false,
//	  "state": "DRAFT",
//	  "publishDate": null,
//	  "views": 0,
//	  "username": "user1",
//	  "categories": [
//	    {
//	      "categoryId": 3,
//	      "categoryName": "eSports"
//	    },
//	    {
//	      "categoryId": 4,
//	      "categoryName": "RPG"
//	    }
//	  ]
//	}
    @PutMapping("/article/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Integer id, @RequestBody ArticleDto articleDto) {
        return articleService.updateArticle(id, articleDto);
    }
    
    // Eliminar un artículo
    @DeleteMapping("/article/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Integer id) {
        return articleService.deleteArticle(id);
    }
	
    // Reportar un artículo
    @PutMapping("/article/{id}/report")
    @Operation(summary = "Reportar un artículo", 
              description = "Marca un artículo como reportado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Artículo reportado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Artículo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> reportArticle(
        @Parameter(description = "ID del artículo a reportar", required = true)
        @PathVariable Integer id) {
        return articleService.reportArticle(id);
    }

    // Desreportar un artículo
    @PutMapping("/article/{id}/unreport")
    @Operation(summary = "Desreportar un artículo", 
              description = "Quita el reporte de un artículo (requiere rol ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Artículo desreportado exitosamente"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para esta acción"),
        @ApiResponse(responseCode = "404", description = "Artículo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> unreportArticle(
        @Parameter(description = "ID del artículo a desreportar", required = true)
        @PathVariable Integer id) {
        return articleService.unreportArticle(id);
    }
}