package com.checkpointBlog.model;

public class UserDto {
    private String username;
    private String name;
    private String email;
    private Role role;

    public UserDto(String username, String name, String email, Role role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
