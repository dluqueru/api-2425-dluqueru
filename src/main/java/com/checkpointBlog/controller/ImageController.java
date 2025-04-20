package com.checkpointBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.checkpointBlog.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Imágenes", description = "Operaciones relacionadas con imágenes")
public class ImageController {

	@Autowired
	private ImageService imageService;
	
	// Lista de imágenes por artículo
	@GetMapping("/images/{articleId}")
	@Operation(summary = "Obtener todas las imágenes de un artículo", description = "Devuelve una lista con todas las imágenes presentes en la base de datos para el artículo con Id pasado como parámetro")
	@ApiResponses({
		 @ApiResponse(responseCode = "200", description = "Imágenes encontradas"),
		 @ApiResponse(responseCode = "500", description = "Error interno del servidor")
		})
	public ResponseEntity<?> listImages(
			@Parameter(description = "ID del artículo", example = "1", required = true)
			@PathVariable Integer articleId) {
		return imageService.getImagesByArticleId(articleId);
	}
}