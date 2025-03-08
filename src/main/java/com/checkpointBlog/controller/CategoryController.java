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

import com.checkpointBlog.model.Category;
import com.checkpointBlog.service.CategoryService;

@RestController
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	 // Lista de categorías
    @GetMapping("/category")
    public ResponseEntity<?> getCategories() {
        return categoryService.getCategories();
    }

    // Obtener categoría por id
    @GetMapping("/category/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategory(id);
    }

    // Crear una nueva categoría
//	FORMATO PARA AÑADIR    
//  {
//	  "name": "Prueba",
//	  "description": "Prueba de prueba"
//	}
    @PostMapping("/category")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Actualizar una categoría existente
//	FORMATO PARA EDITAR    
//  {
//	  "name": "Prueba",
//	  "description": "Prueba de prueba"
//	}
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    // Eliminar una categoría
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id);
    }
    
}