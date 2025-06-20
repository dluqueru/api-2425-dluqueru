package com.checkpointBlog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
@Table(name="User")
public class User implements UserDetails{

	private static final long serialVersionUID = 1L;

	@Id
	private String username;
	
	@Column(name="name")
	private String name;
	
	@Email
	@Column(name="email")
	private String email;
	
	@Column(name="password")
	private String password;
	
	@Column(name="image_url")
	private String photo;
	
	@Column(name="image_public_id")
	private String photoPublicId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="role")
	private Role role;
	
	@Column(name="reputation")
	private int reputation;

	public User() {
		super();
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhotoPublicId() {
		return photoPublicId;
	}

	public void setPhotoPublicId(String photoPublicId) {
		this.photoPublicId = photoPublicId;
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
	
	public void likeIncrementReputation() {
		this.reputation++;
		if(this.getRole() == Role.READER && this.reputation == 5) {
			this.setRole(Role.EDITOR);
		}
	}
	
	public void dislikeDecrementReputation() {
		this.reputation--;
		if(this.getRole() == Role.EDITOR && this.reputation < 5) {
			this.setRole(Role.READER);
		}
	}
	
	public void createArticleIncrementReputation() {
		this.reputation+=5;
	}
	
	public boolean canCreateArticle() {
	    return this.reputation >= 5;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> list = new ArrayList<>();
		list.add(new SimpleGrantedAuthority(role.toString()));
		
		return list;
	}
}
