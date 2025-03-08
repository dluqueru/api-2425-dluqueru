package com.checkpointBlog.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Article")
@Schema(description = "Entidad que representa un artículo en la aplicación")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "ID del artículo", example = "1")
    private Integer id;

    @Column(name = "title", nullable = false)
    @Schema(description = "Título del artículo", example = "Reseña de The Legend of Zelda")
    private String title;

    @Column(name = "body", nullable = false)
    @Schema(description = "Cuerpo del artículo", example = "Análisis detallado del juego The Legend of Zelda y su impacto en la industria.")
    private String body;

    @Column(name = "reported", nullable = false)
    @Schema(description = "Valor que marca si un artículo está reportado o no", example = "true")
    private boolean reported;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Schema(description = "Estado del artículo ('draft' o 'definitive')", example = "definitive")
    private State state;

    @Column(name = "publish_date")
    @Schema(description = "Fecha de publicación del artículo", example = "2024-02-01T10:00:00")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime publishDate;

    @Column(name = "views", nullable = false)
    @Schema(description = "Número de vistas del artículo", example = "150")
    private int views;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    @Schema(description = "Username del usuario que ha creado el artículo", example = "user1")
    private User user;
    
    @OneToMany(mappedBy="article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleCategory> articleCategories;

    public Article() {
        super();
        this.publishDate = LocalDateTime.now();
        this.views = 0; // Se inicializa las vistas en 0
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (obj == null || getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        return Objects.equals(id, other.id);
    }
}
