package com.checkpointBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkpointBlog.model.User;

public interface UserRepository extends JpaRepository<User, String> {

}
