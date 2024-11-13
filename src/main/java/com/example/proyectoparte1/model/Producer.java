package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Producer",
        description = "Representación de un productor, incluyendo el nombre, logo y país de origen"
)
public class Producer {

    @Schema(
            description = "Nombre del productor o estudio de producción",
            example = "Warner Bros. Pictures"
    )
    private String name;

    @Schema(
            description = "URL del logo del productor o estudio de producción",
            example = "https://example.com/logo.jpg"
    )
    private String logo;

    @Schema(
            description = "País de origen del productor o estudio de producción",
            example = "Estados Unidos"
    )
    private String country;

    // Constructor por defecto
    public Producer() {}

    // Constructor completo
    public Producer(String name, String logo, String country) {
        this.name = name;
        this.logo = logo;
        this.country = country;
    }

    // Getters y Setters con estilo encadenado
    public String getName() { return name; }
    public Producer setName(String name) { this.name = name; return this; }
    public String getLogo() { return logo; }
    public Producer setLogo(String logo) { this.logo = logo; return this; }
    public String getCountry() { return country; }
    public Producer setCountry(String country) { this.country = country; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return Objects.equals(name, producer.name) &&
                Objects.equals(logo, producer.logo) &&
                Objects.equals(country, producer.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, logo, country);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Producer.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("logo='" + logo + "'")
                .add("country='" + country + "'")
                .toString();
    }
}
