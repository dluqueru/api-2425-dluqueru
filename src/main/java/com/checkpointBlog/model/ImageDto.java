package com.checkpointBlog.model;

import java.util.Objects;

public class ImageDto {

    private Integer id;
    private String imageUrl;
    private Integer articleId;

    public ImageDto(Integer id, String imageUrl, Integer articleId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.articleId = articleId;
    }
    
    public ImageDto(Image image) {
        this.id = image.getId();
        this.imageUrl = image.getImageUrl();
        this.articleId = image.getArticle().getId();
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
		return id == other.id;
	}
}
