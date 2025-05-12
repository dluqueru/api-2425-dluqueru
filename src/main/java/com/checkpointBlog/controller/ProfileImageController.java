package com.checkpointBlog.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.checkpointBlog.service.ImageService;
import com.checkpointBlog.service.UserService;

@RestController
@RequestMapping("/api/users/profile")
public class ProfileImageController {

    private final ImageService imageService;
    private final UserService userService;

    public ProfileImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @PostMapping("/{username}/upload")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable String username,
            @RequestParam MultipartFile file) {

        ResponseEntity<?> uploadResponse = imageService.uploadAndSaveProfileImage(file);
        if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
            return uploadResponse;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) uploadResponse.getBody();
        return userService.updateUserProfileImage(username, responseBody.get("imageUrl"), responseBody.get("publicId"));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteProfileImage(@PathVariable String username) {
        ResponseEntity<?> userResponse = userService.getUserProfileImageInfo(username);
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return userResponse;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> userInfo = (Map<String, String>) userResponse.getBody();
        String publicId = userInfo.get("publicId");
        
        if (publicId == null || publicId.isEmpty()) {
            return ResponseEntity.badRequest().body("El usuario no tiene imagen de perfil");
        }

        ResponseEntity<?> deleteResponse = imageService.deleteProfileImage(publicId);
        if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
            return deleteResponse;
        }
        
        return userService.updateUserProfileImage(username, null, null);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getProfileImageUrl(@PathVariable String username) {
        return userService.getUserProfileImageInfo(username);
    }
}