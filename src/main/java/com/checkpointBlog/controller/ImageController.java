package com.checkpointBlog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.checkpointBlog.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Imágenes", description = "Operaciones relacionadas con imágenes")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload/{articleId}")
    @Operation(summary = "Subir imagen para artículo", 
               description = "Sube una imagen a Cloudinary y la asocia a un artículo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Artículo no encontrado"),
        @ApiResponse(responseCode = "415", description = "Tipo de archivo no soportado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "ID del artículo", required = true)
            @PathVariable Integer articleId,
            @Parameter(description = "Archivo de imagen (JPEG, PNG, WEBP)", required = true)
            @RequestParam MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
        }
        
        return imageService.uploadAndSaveImage(articleId, file);
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Obtener imágenes por artículo", 
               description = "Devuelve todas las imágenes asociadas a un artículo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de imágenes obtenida"),
        @ApiResponse(responseCode = "204", description = "No hay imágenes para este artículo"),
        @ApiResponse(responseCode = "404", description = "Artículo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getImagesByArticleId(
            @Parameter(description = "ID del artículo", example = "1", required = true)
            @PathVariable Integer articleId) {
        return imageService.getImagesByArticleId(articleId);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Eliminar imagen", 
               description = "Elimina una imagen de Cloudinary y de la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error al eliminar la imagen")
    })
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "ID de la imagen", required = true)
            @PathVariable Integer imageId) {
        return imageService.deleteImage(imageId);
    }
}