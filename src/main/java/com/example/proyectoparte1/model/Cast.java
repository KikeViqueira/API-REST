package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Cast",
        description = "Representación de un miembro del reparto con información del personaje interpretado"
)
public class Cast extends Person {

    @Schema(
            description = "Nombre del personaje interpretado por el miembro del reparto",
            example = "Cobb"
    )
    private String character;

    // Constructor por defecto
    public Cast() {}

    // Constructor completo
    public Cast(String id, String name, String country, String picture, String biography, DateCustom birthday, DateCustom deathday, String character) {
        super(id, name, country, picture, biography, birthday, deathday);
        this.character = character;
    }

    // Getter y Setter con estilo encadenado
    public String getCharacter() { return character; }
    public Cast setCharacter(String character) { this.character = character; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cast cast = (Cast) o;
        return Objects.equals(character, cast.character);
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Cast.class.getSimpleName() + "[", "]")
                .add("character='" + character + "'")
                .toString();
    }
}
