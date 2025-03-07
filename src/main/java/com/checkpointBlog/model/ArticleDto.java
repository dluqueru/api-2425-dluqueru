package com.checkpointBlog.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArticleDto {
	
	private Integer id;
	private String title;
	private String body;
	private boolean reported;
	private State state;
	private LocalDateTime publishDate;
	private int views;
	private String username;
	private List<ArticleCategoryDto> categories;

	public ArticleDto(Article a) {
	    this.id = a.getId();
	    this.title = a.getTitle();
	    this.body = a.getBody();
	    this.reported = a.isReported();
	    this.state = a.getState();
	    this.publishDate = a.getPublishDate();
	    this.views = a.getViews();
	    this.categories = a.getArticleCategories().stream()
	        .map(ac -> new ArticleCategoryDto(ac.getArticle().getId(), ac.getCategory().getId(), ac.getCategory().getName()))
	        .collect(Collectors.toList());
	    this.username = a.getUser().getUsername();
	}

	public ArticleDto(Integer id, String title, String body) {
		this.id = id;
		this.title = title;
		this.body = body;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isReported() {
		return reported;
	}

	public void setReported(boolean reported) {
		this.reported = reported;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LocalDateTime getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(LocalDateTime publishDate) {
		this.publishDate = publishDate;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<ArticleCategoryDto> getCategories() {
		return categories;
	}

	public void setCategories(List<ArticleCategoryDto> categories) {
		this.categories = categories;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		ArticleDto other = (ArticleDto) obj;
		return Objects.equals(id, other.id);
	}
}