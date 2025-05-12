package com.checkpointBlog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);

}
