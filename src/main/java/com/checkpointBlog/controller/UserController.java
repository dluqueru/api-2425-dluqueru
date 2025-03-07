package com.checkpointBlog.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.checkpointBlog.exception.BadCredentialException;
import com.checkpointBlog.model.Credential;
import com.checkpointBlog.model.User;
import com.checkpointBlog.secutiry.TokenUtils;
import com.checkpointBlog.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder code;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
//	@GetMapping("/addUser")
//	public ResponseEntity<?> addUser(){
//		User user = new User();
//		user.setUsername("admin");
//		user.setPassword(code.encode("admin"));
//		user.setRole("admin");
//		
//		user = userService.save(user);
//		
//		return ResponseEntity.ok(user);
//	}
	
	@PostMapping("/login")
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
	
	
	
	
}