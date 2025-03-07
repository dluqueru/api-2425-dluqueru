package com.checkpointBlog.model;

import java.io.Serializable;
import java.util.Objects;

public class ArticleCategoryId implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer article;
	private Integer category;
	
	public ArticleCategoryId() {
		super();
	}

	public Integer getArticle() {
		return article;
	}

	public void setArticle(Integer article) {
		this.article = article;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(article, category);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleCategoryId other = (ArticleCategoryId) obj;
		return Objects.equals(article, other.article) && Objects.equals(category, other.category);
	}
}
