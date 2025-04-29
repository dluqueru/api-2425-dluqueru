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
    
    @Column(name="image_url", length = 512)
    @Schema(description = "URL segura de la imagen en Cloudinary", 
            example = "https://res.cloudinary.com/tu_cloud/image/upload/v123/example.jpg")
    private String imageUrl;
    
    @Column(name="public_id", length = 255)
    @Schema(description = "Identificador único de la imagen en Cloudinary", 
            example = "checkpointBlog/example123")
    private String publicId;
    
    @Column(name="asset_id", length = 255)
    @Schema(description = "ID del asset en Cloudinary", 
            example = "a1b2c3d4e5f6g7h8i9j0")
    private String assetId;
    
    @Column(name="format", length = 10)
    @Schema(description = "Formato de la imagen", example = "jpg")
    private String format;
    
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

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
        return Objects.equals(id, other.id);
    }
}