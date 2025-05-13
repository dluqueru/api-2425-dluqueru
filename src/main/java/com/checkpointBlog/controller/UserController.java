package com.checkpointBlog.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.checkpointBlog.model.ArticleDto;
import com.checkpointBlog.model.Credential;
import com.checkpointBlog.model.Role;
import com.checkpointBlog.model.User;
import com.checkpointBlog.model.UserDto;
import com.checkpointBlog.security.TokenUtils;
import com.checkpointBlog.service.ImageService;
import com.checkpointBlog.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder code;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
    @Autowired
    private ImageService imageService;
	
	// Registro de usuario
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	FORMATO
//	{
//	  "username": "borrar",
//	  "name": "borrar",
//	  "email": "borrar@gmail.com",
//	  "password": "borrar"
//	}
	public ResponseEntity<?> addUser(
	        @RequestParam("username") String username,
	        @RequestParam("name") String name,
	        @RequestParam("email") String email,
	        @RequestParam("password") String password,
	        @RequestParam(value = "file", required = false) MultipartFile file) {

	    if (username == null || username.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("El username es obligatorio");
	    }
	    if (email == null || email.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("El email es obligatorio");
	    }
	    if (password == null || password.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("La contraseña es obligatoria");
	    }

	    if (!isValidPassword(password)) {
	        Map<String, String> response = new HashMap<>();
	        response.put("errorMessage", "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

	    if (userService.existsByUsername(username)) {
	        Map<String, String> response = new HashMap<>();
	        response.put("errorMessage", "El nombre de usuario ya está en uso");
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	    }
	    
	    if (userService.existsByEmail(email)) {
	        Map<String, String> response = new HashMap<>();
	        response.put("errorMessage", "El email ya está registrado");
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	    }

	    String photoUrl = null;
	    String publicId = null;
	    
	    if (file != null && !file.isEmpty()) {
	        try {
	            ResponseEntity<?> uploadResponse = imageService.uploadAndSaveProfileImage(file);
	            System.out.println("Respuesta completa del servicio: " + uploadResponse.getBody()); // Debug
	            
	            if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
	                return uploadResponse;
	            }

	            Object responseBody = uploadResponse.getBody();
	            if (!(responseBody instanceof Map)) {
	                System.err.println("La respuesta no es un Map: " + responseBody);
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("Formato de respuesta inesperado");
	            }
	            
	            @SuppressWarnings("unchecked")
	            Map<String, Object> cloudinaryResponse = (Map<String, Object>) responseBody;

	            photoUrl = (String) cloudinaryResponse.get("secure_url");
	            publicId = (String) cloudinaryResponse.get("public_id");
	            
	            if (photoUrl == null || publicId == null) {
	                System.err.println("Cloudinary no devolvió los campos esperados. Campos disponibles: " + 
	                    cloudinaryResponse.keySet());
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("La respuesta de Cloudinary no contiene los datos esperados");
	            }
	        } catch (Exception e) {
	            System.err.println("Excepción al subir imagen: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error al subir la imagen de perfil");
	        }
	    }

	    try {
	        User newUser = new User();
	        newUser.setUsername(username);
	        newUser.setName(name);
	        newUser.setEmail(email);
	        newUser.setPhoto(photoUrl);
	        newUser.setPhotoPublicId(publicId);
	        newUser.setPassword(code.encode(password));
	        newUser.setRole(Role.READER);

	        User savedUser = userService.save(newUser);

	        UserDto responseUser = new UserDto(
	            savedUser.getUsername(), 
	            savedUser.getName(),
	            savedUser.getEmail(), 
	            savedUser.getPhoto(), 
	            savedUser.getRole()
	        );

	        return ResponseEntity.ok(responseUser);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al registrar el usuario");
	    }
	}
	
	private boolean isValidPassword(String password) {
	    if (password == null) return false;

	    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$";
	    return password.matches(passwordRegex);
	}
	
	// Login de usuario
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/login")
//	FORMATO
//	{
//	  "username": "borrar",
//	  "password": "borrar"
//	}
	public ResponseEntity<?> getToken(@RequestBody Credential credential) throws Exception{
		
		try {
			org.springframework.security.core.Authentication userLogin;
			
			userLogin = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(credential.getUsername(),  credential.getPassword()));
			
			User user = (User) userLogin.getPrincipal();
			
			String token = TokenUtils.generateToken(user.getUsername(), "", user.getRole());
			
			Map<String, String> result = new HashMap<>();
			result.put("token", token);
			
			return ResponseEntity.ok(result);
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	// Lista de usuarios
    @GetMapping("/user")
    public ResponseEntity<?> getUsers() {
        return userService.getUsers();
    }
	
    // Obtener usuario por username
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserById(@PathVariable String username) {
        return userService.getUser(username);
    }
    
    // Actualizar un usuario existente
    @PutMapping("/user/{username}")
// 	FORMATO
//	{
//	  "username": "borrar",
//	  "name": "borradísimo",
//	  "email": "borrar@gmail.com",
//	  "password": "borrar"
//	}
     public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user) {
         return userService.updateUser(username, user);
     }
    
    // Eliminar un usuario
    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }
    
    @PostMapping(value = "/user/{username}/profile-image", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadProfileImage(
	        @PathVariable String username,
	        @RequestParam("file") MultipartFile file) {
	    
	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
	    }
	    
	    try {
	        ResponseEntity<?> uploadResponse = imageService.uploadAndSaveProfileImage(file);
	        if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
	            return uploadResponse;
	        }

	        @SuppressWarnings("unchecked")
	        Map<String, String> responseBody = (Map<String, String>) uploadResponse.getBody();
	        return userService.createOrUpdateUserProfileImage(
	            username, 
	            responseBody.get("imageUrl"), 
	            responseBody.get("publicId")
	        );
	        
	    } catch (Exception e) {
	        return ResponseEntity.internalServerError()
	            .body("Error al procesar la imagen: " + e.getMessage());
	    }
	}
    
    @GetMapping("/user/{username}/profile-image")
    public ResponseEntity<?> getProfileImage(@PathVariable String username) {
        return userService.getUserProfileImageInfo(username);
    }
    
    @DeleteMapping("/user/{username}/profile-image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable String username) {
        ResponseEntity<?> userResponse = userService.getUserProfileImageInfo(username);
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return userResponse;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> userInfo = (Map<String, String>) userResponse.getBody();
        String publicId = userInfo.get("publicId");
        
        if (publicId != null && !publicId.isEmpty()) {
            ResponseEntity<?> deleteResponse = imageService.deleteImage(publicId);
            if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                return deleteResponse;
            }
        }
        
        return userService.updateUserProfileImage(username, null, null);
    }
    
    @GetMapping("user/{username}/liked-articles")
    public ResponseEntity<List<ArticleDto>> getLikedArticles(@PathVariable String username) {
        List<ArticleDto> likedArticles = userService.getLikedArticles(username);
        return ResponseEntity.ok(likedArticles);
    }
}