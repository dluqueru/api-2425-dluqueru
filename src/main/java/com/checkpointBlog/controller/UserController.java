package com.checkpointBlog.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.checkpointBlog.exception.BadCredentialException;
import com.checkpointBlog.model.Category;
import com.checkpointBlog.model.Credential;
import com.checkpointBlog.model.Role;
import com.checkpointBlog.model.User;
import com.checkpointBlog.model.UserDto;
import com.checkpointBlog.security.TokenUtils;
import com.checkpointBlog.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder code;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	// Registro de usuario
	@PostMapping("/register")
//	FORMATO
//	{
//	  "username": "borrar",
//	  "name": "borrar",
//	  "email": "borrar@gmail.com",
//	  "password": "borrar"
//	}
	public ResponseEntity<?> addUser(@Validated @RequestBody User user, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	    	Map<String, String> response = new HashMap<>();
			response.put("errorMessage", bindingResult.getAllErrors().toString());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

	    if (userService.existsByUsername(user.getUsername()) || userService.existsByEmail(user.getEmail())) {
	    	Map<String, String> response = new HashMap<>();
			response.put("errorMessage", "El usuario ya existe");
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	    }

	    User newUser = new User();
	    newUser.setUsername(user.getUsername());
	    newUser.setName(user.getName());
	    newUser.setEmail(user.getEmail());
	    newUser.setPassword(code.encode(user.getPassword()));
	    newUser.setRole(Role.READER);

	    newUser = userService.save(newUser);

	    UserDto responseUser = new UserDto(newUser.getUsername(), newUser.getName(), newUser.getEmail(), newUser.getRole());

	    return ResponseEntity.ok(responseUser);
	}
	
	// Login de usuario
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
	
}