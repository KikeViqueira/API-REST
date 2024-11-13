package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Collection",
        description = "Representación de una colección de películas, con su nombre y recursos asociados"
)
public class Collection {

    @Schema(
            description = "Nombre de la colección de películas",
            example = "The Dark Knight Collection"
    )
    private String name;

    @Schema(
            description = "Lista de recursos asociados a la colección, como imágenes o videos",
            example = "[{\"type\": \"Poster\", \"url\": \"https://example.com/poster.jpg\"}, {\"type\": \"Trailer\", \"url\": \"https://example.com/trailer.mp4\"}]"
    )
    private List<Resource> resources = new ArrayList<>();

    // Constructor por defecto
    public Collection() {}

    // Constructor completo
    public Collection(String name, List<Resource> resources) {
        this.name = name;
        this.resources = resources;
    }

    // Getters y Setters con estilo encadenado
    public String getName() { return name; }
    public Collection setName(String name) { this.name = name; return this; }
    public List<Resource> getResources() { return resources; }
    public Collection setResources(List<Resource> resources) { this.resources = resources; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return Objects.equals(name, that.name) && Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, resources);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Collection.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("resources=" + resources)
                .toString();
    }
}
