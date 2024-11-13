package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Document(collection = "films")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Movie",
        description = "Representación completa de una película con todos sus datos y relaciones"
)
public class Movie {

    @Id
    @Schema(
            description = "ID único de la película en la base de datos",
            example = "615d1b2f4f1a4e6f1a4e6f1a"
    )
    private String id;

    @NotBlank(message = "El título de la película no puede ser vacío")
    @Size(min = 1, message = "El título debe tener al menos un carácter")
    @Schema(
            description = "Título de la película",
            example = "Inception",
            required = true
    )
    private String title;

    @Schema(
            description = "Resumen o sinopsis de la película",
            example = "Un ladrón que roba secretos corporativos a través del uso de la tecnología de sueños compartidos..."
    )
    private String overview;

    @Schema(
            description = "Frase o eslogan asociado a la película",
            example = "Your mind is the scene of the crime."
    )
    private String tagline;

    @Schema(
            description = "Colección a la que pertenece la película, si aplica",
            example = "{ \"name\": \"The Dark Knight Collection\" }"
    )
    private Collection collection;

    @Schema(
            description = "Lista de géneros asociados a la película",
            example = "[\"Action\", \"Thriller\", \"Science Fiction\"]"
    )
    private List<String> genres;

    @Schema(
            description = "Fecha de estreno personalizada de la película",
            example = "2021-12-25"
    )
    private DateCustom releaseDateCustom;

    @Schema(
            description = "Palabras clave relacionadas con la película",
            example = "[\"dream\", \"subconscious\", \"heist\"]"
    )
    private List<String> keywords;

    @Schema(
            description = "Lista de productores de la película",
            example = "[{\"name\": \"Christopher Nolan\"}, {\"name\": \"Emma Thomas\"}]"
    )
    private List<Producer> producers;

    @Schema(
            description = "Lista del equipo técnico de la película",
            example = "[{\"name\": \"Hans Zimmer\", \"job\": \"Composer\"}]"
    )
    private List<Crew> crew;

    @Schema(
            description = "Lista de miembros del reparto de la película",
            example = "[{\"name\": \"Leonardo DiCaprio\", \"character\": \"Cobb\"}]"
    )
    private List<Cast> cast;

    @Schema(
            description = "Recursos adicionales como imágenes o videos",
            example = "[{\"type\": \"Poster\", \"url\": \"https://example.com/poster.jpg\"}]"
    )
    private List<Resource> resources;

    @Schema(
            description = "Presupuesto de la película en dólares",
            example = "160000000"
    )
    private Long budget;

    @Schema(
            description = "Estado de la película, indicando si está lanzada, en producción, etc.",
            example = "RELEASED"
    )
    private Status status;

    @Schema(
            description = "Duración de la película en minutos",
            example = "148"
    )
    private Integer runtime;

    @Schema(
            description = "Ingresos generados por la película en dólares",
            example = "830000000"
    )
    private Long revenue;

    // Constructor por defecto
    public Movie() {}

    // Constructor completo
    public Movie(String id, String title, String overview, String tagline, Collection collection, List<String> genres, DateCustom releaseDateCustom, List<String> keywords, List<Producer> producers, List<Crew> crew, List<Cast> cast, List<Resource> resources, Long budget, Status status, Integer runtime, Long revenue) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.tagline = tagline;
        this.collection = collection;
        this.genres = genres;
        this.releaseDateCustom = releaseDateCustom;
        this.keywords = keywords;
        this.producers = producers;
        this.crew = crew;
        this.cast = cast;
        this.resources = resources;
        this.budget = budget;
        this.status = status;
        this.runtime = runtime;
        this.revenue = revenue;
    }

    // Getters y Setters con estilo encadenado
    public String getId() { return id; }
    public Movie setId(String id) { this.id = id; return this; }
    public String getTitle() { return title; }
    public Movie setTitle(String title) { this.title = title; return this; }
    public String getOverview() { return overview; }
    public Movie setOverview(String overview) { this.overview = overview; return this; }
    public String getTagline() { return tagline; }
    public Movie setTagline(String tagline) { this.tagline = tagline; return this; }
    public Collection getCollection() { return collection; }
    public Movie setCollection(Collection collection) { this.collection = collection; return this; }
    public List<String> getGenres() { return genres; }
    public Movie setGenres(List<String> genres) { this.genres = genres; return this; }
    public DateCustom getReleaseDate() { return releaseDateCustom; }
    public Movie setReleaseDate(DateCustom releaseDateCustom) { this.releaseDateCustom = releaseDateCustom; return this; }
    public List<String> getKeywords() { return keywords; }
    public Movie setKeywords(List<String> keywords) { this.keywords = keywords; return this; }
    public List<Producer> getProducers() { return producers; }
    public Movie setProducers(List<Producer> producers) { this.producers = producers; return this; }
    public List<Crew> getCrew() { return crew; }
    public Movie setCrew(List<Crew> crew) { this.crew = crew; return this; }
    public List<Cast> getCast() { return cast; }
    public Movie setCast(List<Cast> cast) { this.cast = cast; return this; }
    public List<Resource> getResources() { return resources; }
    public Movie setResources(List<Resource> resources) { this.resources = resources; return this; }
    public Long getBudget() { return budget; }
    public Movie setBudget(Long budget) { this.budget = budget; return this; }
    public Status getStatus() { return status; }
    public Movie setStatus(Status status) { this.status = status; return this; }
    public Integer getRuntime() { return runtime; }
    public Movie setRuntime(Integer runtime) { this.runtime = runtime; return this; }
    public Long getRevenue() { return revenue; }
    public Movie setRevenue(Long revenue) { this.revenue = revenue; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id) &&
                Objects.equals(title, movie.title) &&
                Objects.equals(overview, movie.overview) &&
                Objects.equals(tagline, movie.tagline) &&
                Objects.equals(collection, movie.collection) &&
                Objects.equals(genres, movie.genres) &&
                Objects.equals(releaseDateCustom, movie.releaseDateCustom) &&
                Objects.equals(keywords, movie.keywords) &&
                Objects.equals(producers, movie.producers) &&
                Objects.equals(crew, movie.crew) &&
                Objects.equals(cast, movie.cast) &&
                Objects.equals(resources, movie.resources) &&
                Objects.equals(budget, movie.budget) &&
                status == movie.status &&
                Objects.equals(runtime, movie.runtime) &&
                Objects.equals(revenue, movie.revenue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, overview, tagline, collection, genres, releaseDateCustom, keywords, producers, crew, cast, resources, budget, status, runtime, revenue);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Movie.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("title='" + title + "'")
                .add("overview='" + overview + "'")
                .add("tagline='" + tagline + "'")
                .add("collection=" + collection)
                .add("genres=" + genres)
                .add("releaseDate=" + releaseDateCustom)
                .add("keywords=" + keywords)
                .add("producers=" + producers)
                .add("crew=" + crew)
                .add("cast=" + cast)
                .add("resources=" + resources)
                .add("budget=" + budget)
                .add("status=" + status)
                .add("runtime=" + runtime)
                .add("revenue=" + revenue)
                .toString();
    }
}
