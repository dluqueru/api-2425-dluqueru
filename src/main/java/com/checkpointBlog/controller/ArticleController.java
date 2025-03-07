package com.checkpointBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.checkpointBlog.exception.ElementNotFoundException;
import com.checkpointBlog.model.Article;
import com.checkpointBlog.secutiry.TokenUtils;
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
	
//	@GetMapping("/login")
//	public ResponseEntity<?> getToken(){
//		String token = TokenUtils.generateToken("daniel", "daniel@gmail.com", "user");
//		return ResponseEntity.ok(token);
//	}
	
	// Lista de artículos
	@GetMapping("/article")
	@Operation(summary = "Obtener todos los artículos", description = "Devuelve una lista con todos los artículos presentes en la base de datos")
	@ApiResponses({
		 @ApiResponse(responseCode = "200", description = "Artículos encontrados"),
		 @ApiResponse(responseCode = "500", description = "Error interno del servidor")
		})
	public ResponseEntity<?> listArticles() {
		return articleService.getArticles();
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
	
	// Añadir un artículo
    @PostMapping("/article")
    public ResponseEntity<?> addArticle(@RequestBody Article article) {
        return articleService.addArticle(article);
    }
    
    // Editar un artículo
    @PutMapping("/article/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Integer id, @RequestBody Article article) {
        return articleService.updateArticle(id, article);
    }
    
    // Eliminar un artículo
    @DeleteMapping("/article/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Integer id) {
        return articleService.deleteArticle(id);
    }
	
}