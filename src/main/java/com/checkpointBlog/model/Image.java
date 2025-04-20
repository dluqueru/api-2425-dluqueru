package com.checkpointBlog.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Image")
@Schema(description = "Entidad que representa una imagen en la aplicación")
public class Image {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name="id")
		@Schema(description = "ID de la imagen", example = "1")
		private Integer id;
		
		@Column(name="image")
		@Schema(description = "Url de la imagen", example = "https://upload.wikimedia.org/wikipedia/commons/0/0a/The_International_2014.jpg")
		private String imageUrl;
		
		@ManyToOne
	    @JoinColumn(name="article_id")
	    private Article article;
		
		public Image() {
			super();
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Article getArticle() {
			return article;
		}

		public void setArticle(Article article) {
			this.article = article;
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
			Image other = (Image) obj;
			return id == other.id;
		}
}
