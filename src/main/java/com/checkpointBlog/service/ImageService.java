package com.checkpointBlog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.checkpointBlog.exception.NotFoundException;
import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.Image;
import com.checkpointBlog.model.ImageDto;
import com.checkpointBlog.repository.ArticleRepository;
import com.checkpointBlog.repository.ImageRepository;

@Service
public class ImageService {
    
    private final ImageRepository imageRepository;
    private final ArticleRepository articleRepository;
    private final CloudinaryService cloudinaryService;

    public ImageService(ImageRepository imageRepository, 
                      ArticleRepository articleRepository,
                      CloudinaryService cloudinaryService) {
        this.imageRepository = imageRepository;
        this.articleRepository = articleRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public ResponseEntity<?> uploadAndSaveImage(Integer articleId, MultipartFile file) {
        try {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("Artículo no encontrado"));
            
            Map<?, ?> uploadResult = cloudinaryService.uploadFile(file);
            Image image = createImageFromUploadResult(uploadResult, article);
            
            Image savedImage = imageRepository.save(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ImageDto(savedImage));
            
        } catch (NotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Artículo no encontrado", e);
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                                   "Error al subir la imagen", e);
        }
    }

    public ResponseEntity<?> getImagesByArticleId(Integer articleId) {
        try {
            if (!articleRepository.existsById(articleId)) {
                throw new NotFoundException("Artículo no encontrado");
            }
            
            List<Image> images = imageRepository.findImagesByArticleId(articleId);
            
            if (images.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            
            List<ImageDto> dtos = images.stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(dtos);
            
        } catch (NotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                                   "Error al obtener imágenes", e);
        }
    }

    @Transactional
    public ResponseEntity<?> deleteImage(Integer imageId) {
        try {
            Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Imagen no encontrada"));
            
            cloudinaryService.deleteImage(image.getPublicId());
            imageRepository.delete(image);
            
            return ResponseEntity.ok().build();
            
        } catch (NotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                                   "Error al eliminar la imagen", e);
        }
    }

    private Image createImageFromUploadResult(Map<?, ?> uploadResult, Article article) {
        Image image = new Image();
        image.setImageUrl(uploadResult.get("secure_url").toString());
        image.setPublicId(uploadResult.get("public_id").toString());
        image.setAssetId(uploadResult.get("asset_id").toString());
        image.setFormat(uploadResult.get("format").toString());
        image.setArticle(article);
        return image;
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(
            HttpStatus status, String error, Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}