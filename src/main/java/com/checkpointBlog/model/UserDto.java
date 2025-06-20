package com.checkpointBlog.model;

public class UserDto {
    private String username;
    private String name;
    private String email;
    private String photo;
    private Role role;
    private int reputation;

    public UserDto(String username, String name, String email, String photo, Role role, int reputation) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.role = role;
    }

    public UserDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.photo = user.getPhoto();
        this.role = user.getRole();
        this.reputation = user.getReputation();
    }

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}
}
