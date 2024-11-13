package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Resource",
        description = "Representación de un recurso multimedia asociado a una película, como un póster, trailer o imagen de fondo"
)
public class Resource {

    @Schema(
            description = "URL del recurso multimedia",
            example = "https://example.com/resource.jpg",
            required = true
    )
    private String url;

    @Schema(
            description = "Tipo de recurso multimedia, como PÓSTER o TRAILER",
            example = "POSTER",
            required = true,
            implementation = ResourceType.class
    )
    private ResourceType type;

    // Constructor completo
    public Resource(ResourceType type, String url) {
        this.type = type;
        this.url = url;
    }

    // Constructor por defecto
    public Resource() {}

    // Getters y Setters con estilo encadenado
    public ResourceType getType() { return type; }
    public Resource setType(ResourceType type) { this.type = type; return this; }
    public String getUrl() { return url; }
    public Resource setUrl(String url) { this.url = url; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(url, resource.url) && type == resource.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, type);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Resource.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("type=" + type)
                .toString();
    }
}
