package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.StringJoiner;

@Document(collection = "comments")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Assessment",
        description = "Representación de una valoración de película realizada por un usuario"
)
public class Assessment {

    @Id
    @Schema(
            description = "ID único de la valoración",
            example = "5f9f1b9b1f1a4e6f1a4e6f1a"
    )
    private String id;

    @Schema(
            description = "Valoración numérica de la película, entre 1 y 10",
            example = "8",
            minimum = "1",
            maximum = "10"
    )
    private Integer rating;

    @Schema(
            description = "Información básica del usuario que realiza la valoración, incluyendo solo nombre y email",
            example = "{ \"email\": \"user@example.com\", \"name\": \"Juan Pérez\" }"
    )
    private User user;

    @Schema(
            description = "Información básica de la película que se está valorando, incluyendo solo ID y título",
            example = "{ \"id\": \"movie123\", \"title\": \"Inception\" }"
    )
    private Movie movie;

    @Schema(
            description = "Comentario opcional que acompaña a la valoración de la película",
            example = "Excelente película con una trama intrigante y grandes efectos visuales."
    )
    private String comment;

    // Constructor por defecto
    public Assessment() {}

    // Constructor completo
    public Assessment(String id, Integer rating, User user, Movie movie, String comment) {
        this.id = id;
        this.rating = rating;
        this.user = user;
        this.movie = movie;
        this.comment = comment;
    }

    // Getters
    public String getId() { return id; }
    public Integer getRating() { return rating; }
    public User getUser() { return user; }
    public Movie getMovie() { return movie; }
    public String getComment() { return comment; }

    // Setters con estilo encadenado
    public Assessment setId(String id) { this.id = id; return this; }
    public Assessment setRating(Integer rating) { this.rating = rating; return this; }
    public Assessment setUser(User user) { this.user = user; return this; }
    public Assessment setMovie(Movie movie) { this.movie = movie; return this; }
    public Assessment setComment(String comment) { this.comment = comment; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assessment that = (Assessment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(user, that.user) &&
                Objects.equals(movie, that.movie) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, user, movie, comment);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Assessment.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("rating=" + rating)
                .add("user=" + user)
                .add("movie=" + movie)
                .add("comment='" + comment + "'")
                .toString();
    }
}
