package com.checkpointBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.checkpointBlog.model.User;
import com.checkpointBlog.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
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
	
}