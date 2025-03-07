package com.checkpointBlog.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryDto {

	private Integer id;
    private String name;
    private String description;
    private List<CategoryArticleDto> articles;

    public CategoryDto(Category c) {
        this.id = c.getId();
        this.name = c.getName();
        this.description = c.getDescription();
        this.articles = c.getArticleCategories().stream()
                .map(article -> new CategoryArticleDto(article.getArticle().getId(), article.getArticle().getTitle()))
                .collect(Collectors.toList());
    }

    public CategoryDto(Integer id, String name, String description, List<CategoryArticleDto> articles) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.articles = articles;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryArticleDto> getArticles() {
        return articles;
    }

    public void setArticles(List<CategoryArticleDto> articles) {
        this.articles = articles;
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryDto other = (CategoryDto) obj;
		return Objects.equals(id, other.id);
	}
}
