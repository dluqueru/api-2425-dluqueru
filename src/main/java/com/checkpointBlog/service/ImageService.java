package com.checkpointBlog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.Image;
import com.checkpointBlog.model.ImageDto;
import com.checkpointBlog.repository.ArticleRepository;
import com.checkpointBlog.repository.ImageRepository;

@Service
public class ImageService {
	
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	ArticleRepository articleRepository;
	
	// Lista de imágenes por artículo
	public ResponseEntity<?> getImagesByArticleId(Integer articleId) {
		List<Image> list = null;
		Article a = null;
		
		try {
			list = imageRepository.findImagesByArticleId(articleId);
			a = articleRepository.findById(articleId).orElse(null);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Error en la base de datos");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
		
		if (a == null) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "El artículo no existe");
			return ResponseEntity.status(404).body(response);
		}
		
		if (list.isEmpty()) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "Lista vacía");
			return ResponseEntity.status(210).body(response);
		} else {
			List <ImageDto> listResult = list.stream().map(image -> new ImageDto(image)).toList();
			return ResponseEntity.status(HttpStatus.OK).body(listResult);
		}
		
	}
}