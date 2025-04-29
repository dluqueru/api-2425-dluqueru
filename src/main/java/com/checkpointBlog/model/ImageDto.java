package com.checkpointBlog.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para transferencia de datos de imágenes")
public class ImageDto {

    @Schema(description = "ID de la imagen", example = "1")
    private Integer id;
    
    @Schema(description = "URL segura de la imagen en Cloudinary", 
            example = "https://res.cloudinary.com/tu_cloud/image/upload/v123/example.jpg")
    private String imageUrl;
    
    @Schema(description = "Identificador único de la imagen en Cloudinary", 
            example = "checkpointBlog/example123")
    private String publicId;
    
    @Schema(description = "Formato del archivo de imagen", example = "jpg")
    private String format;
    
    @Schema(description = "ID del artículo asociado", example = "5")
    private Integer articleId;

    public ImageDto() {
    }

    public ImageDto(Integer id, String imageUrl, String publicId, String format, Integer articleId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.publicId = publicId;
        this.format = format;
        this.articleId = articleId;
    }
    
    public ImageDto(Image image) {
        this.id = image.getId();
        this.imageUrl = image.getImageUrl();
        this.publicId = image.getPublicId();
        this.format = image.getFormat();
        this.articleId = image.getArticle() != null ? image.getArticle().getId() : null;
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
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
        ImageDto other = (ImageDto) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "ImageDto [id=" + id + ", imageUrl=" + imageUrl + ", publicId=" + publicId + 
               ", format=" + format + ", articleId=" + articleId + "]";
    }
}