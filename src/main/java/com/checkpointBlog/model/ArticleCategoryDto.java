package com.checkpointBlog.model;

import java.util.Objects;

public class ArticleCategoryDto {
	private Integer articleId;
	private Integer categoryId;
	private String categoryName;

    public ArticleCategoryDto(Integer articleId, Integer categoryId, String categoryName) {
        this.articleId = articleId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(articleId, categoryId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleCategoryDto other = (ArticleCategoryDto) obj;
		return Objects.equals(articleId, other.articleId) && Objects.equals(categoryId, other.categoryId);
	}

}
