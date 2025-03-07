package com.checkpointBlog.model;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Category")
@Schema(description = "Entidad que representa una categoría en la aplicación")
public class Category {
		
		@Id
		@Column(name="id")
		@Schema(description = "ID de la categoría", example = "1")
		private Integer id;
		
		@Column(name="name")
		@Schema(description = "Nombre de la categoría", example = "Estrategia")
		private String name;
		
		@Column(name="description")
		@Schema(description = "Descripción de la categoría", example = "Juegos que requieren planificación y táctica para ganar.")
		private String description;
		
		@OneToMany(mappedBy="category")
	    private List<ArticleCategory> articleCategories;
		
		public Category() {
			super();
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

		public List<ArticleCategory> getArticleCategories() {
			return articleCategories;
		}

		public void setArticleCategories(List<ArticleCategory> articleCategories) {
			this.articleCategories = articleCategories;
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
			Category other = (Category) obj;
			return id == other.id;
		}
}
