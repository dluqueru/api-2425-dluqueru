package com.checkpointBlog.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Article_Category")
@IdClass(ArticleCategoryId.class)
public class ArticleCategory {
	
	@Id
	@ManyToOne
    @JoinColumn(name="article_id")
	private Article article;
	
	@Id
	@ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    public ArticleCategory() {
        super();
    }

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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
		ArticleCategory other = (ArticleCategory) obj;
		return Objects.equals(article, other.article) && Objects.equals(category, other.category);
	}

}
