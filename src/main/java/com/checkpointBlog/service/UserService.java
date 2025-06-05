package com.checkpointBlog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.checkpointBlog.exception.NotFoundException;
import com.checkpointBlog.model.Article;
import com.checkpointBlog.model.ArticleDto;
import com.checkpointBlog.model.User;
import com.checkpointBlog.model.UserDto;
import com.checkpointBlog.repository.LikeRepository;
import com.checkpointBlog.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private BCryptPasswordEncoder code;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findById(username).orElse(null);
		
		if(user == null) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		} else {
			return user;
		}
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	// Lista de usuarios
	public ResponseEntity<?> getUsers() {
		List<User> list = null;
		
		try {
			list = userRepository.findAll();
			
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
			List<UserDto> dtoList = list.stream().map(user -> new UserDto(user)).toList();
			return ResponseEntity.status(HttpStatus.OK).body(dtoList);
		}
	}

	// Obtener usuario por su username
	public ResponseEntity<?> getUser (String username) {
		User u = null;
		
		try {
			u = userRepository.findById(username).orElse(null);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Error en la base de datos");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
		
		if (u == null) {
			Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "El usuario no existe");
			return ResponseEntity.status(404).body(response);
		}
		UserDto userDto = new UserDto(u);
		return ResponseEntity.status(200).body(userDto);
		
	}

	public boolean existsByUsername(String username) {
        return userRepository.existsById(username);
    }

	// Eliminar un usuario
    @Transactional
    public ResponseEntity<?> deleteUser(String username) {
    	
    	Optional<User> existingUserOpt = userRepository.findById(username);
    	
    	// Se verifica si el usuario que está borrando tiene el rol de ADMIN
    	if (!hasAdminRole()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Se debe tener el rol de ADMIN para eliminar un usuario");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    	
    	// Se verifica si el usuario existe
        if (existingUserOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "El usuario no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            User deletedUser = existingUserOpt.get();
            userRepository.deleteById(username);
            UserDto userDto = new UserDto(deletedUser);
            return ResponseEntity.status(HttpStatus.OK).body(userDto);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el usuario");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    public boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));
    }
    
    public String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) { 
            return (String) principal;
        }

        return null;
    }

	// Editar un usuario
    public ResponseEntity<?> updateUser(String username, User user) {
        Optional<User> existingUserOptional = userRepository.findById(username);

        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El usuario no existe"));
        }

        if (!this.getLoggedUsername().equals(username) && !this.hasAdminRole()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Sólo un admin o el mismo usuario puede editar este usuario"));
        }

        User existingUser = existingUserOptional.get();

        // No se debe permitir cambiar el username si es el identificador
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        // Si la contraseña ha cambiado, se encripta
        if (!user.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(code.encode(user.getPassword()));
        }

        try {
            userRepository.save(existingUser);
            UserDto responseUser = new UserDto(
                    existingUser.getUsername(), 
                    existingUser.getName(), 
                    existingUser.getEmail(), 
                    existingUser.getPhoto(),
                    existingUser.getRole(),
                    existingUser.getReputation()
            );
            return ResponseEntity.ok(responseUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el usuario", "message", e.getMessage()));
        }
    }

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public ResponseEntity<?> updateUserProfileImage(String username, String imageUrl, String publicId) {
	    try {
	        User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
	        
	        user.setPhoto(imageUrl);
	        user.setPhotoPublicId(publicId);
	        userRepository.save(user);
	        
	        Map<String, String> response = new HashMap<>();
	        response.put("imageUrl", imageUrl);
	        response.put("publicId", publicId);
	        
	        return ResponseEntity.ok(response);
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Error al actualizar la imagen de perfil");
	    }
	}

	public ResponseEntity<?> getUserProfileImageInfo(String username) {
	    try {
	        User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
	        
	        Map<String, String> response = new HashMap<>();
	        response.put("imageUrl", user.getPhoto());
	        response.put("publicId", user.getPhotoPublicId());
	        
	        return ResponseEntity.ok(response);
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Error al obtener la imagen de perfil");
	    }
	}

	public ResponseEntity<?> createOrUpdateUserProfileImage(String username, String imageUrl, String publicId) {
	    User user = userRepository.findByUsername(username)
	        .orElse(new User());
	    
	    user.setUsername(username);
	    user.setPhoto(imageUrl);
	    user.setPhotoPublicId(publicId);
	    
	    user = userRepository.save(user);
	    
	    Map<String, String> response = new HashMap<>();
	    response.put("imageUrl", imageUrl);
	    response.put("publicId", publicId);
	    response.put("username", username);
	    
	    return ResponseEntity.ok(response);
	}
	
	public List<ArticleDto> getLikedArticles(String username) {
        List<Article> articles = likeRepository.findLikedArticlesByUsername(username);
        return articles.stream()
                .map(ArticleDto::new)
                .collect(Collectors.toList());
    }

	public void handleReputationAction(String username, ReputationAction action) {
	    User user = userRepository.findById(username)
	        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	    
	    switch(action) {
	        case ARTICLE_LIKE:
	            user.likeIncrementReputation();
	            break;
	        case ARTICLE_DISLIKE:
	            user.dislikeDecrementReputation();
	            break;
	        case ARTICLE_CREATE:
	            user.createArticleIncrementReputation();
	            break;
	    }
	    
	    userRepository.save(user);
	}

	public enum ReputationAction {
	    ARTICLE_LIKE,
	    ARTICLE_DISLIKE,
	    ARTICLE_CREATE
	}
}