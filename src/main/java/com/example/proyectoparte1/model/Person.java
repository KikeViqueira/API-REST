package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.StringJoiner;

@Document(collection = "people")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Person",
        description = "Representación básica de una persona, utilizada en el contexto de personas relacionadas con películas, como actores o miembros del equipo"
)
public class Person {

    @Id
    @Schema(
            description = "ID único de la persona en la base de datos",
            example = "603d1b2e1f1a4e7f1a4e6f1b"
    )
    private String id;

    @Schema(
            description = "Nombre completo de la persona",
            example = "Christopher Nolan"
    )
    private String name;

    @Schema(
            description = "País de origen de la persona",
            example = "Reino Unido"
    )
    private String country;

    @Schema(
            description = "URL de la imagen de perfil de la persona",
            example = "https://example.com/nolan.jpg"
    )
    private String picture;

    @Schema(
            description = "Biografía breve de la persona",
            example = "Christopher Nolan es un director, productor y guionista británico-estadounidense..."
    )
    private String biography;

    @Schema(
            description = "Fecha de nacimiento de la persona",
            example = "{ \"day\": 30, \"month\": 7, \"year\": 1970 }"
    )
    private DateCustom birthday;

    @Schema(
            description = "Fecha de fallecimiento de la persona, si aplica",
            example = "{ \"day\": 15, \"month\": 1, \"year\": 2021 }"
    )
    private DateCustom deathday;

    // Constructor por defecto
    public Person() {}

    // Constructor completo
    public Person(String id, String name, String country, String picture, String biography, DateCustom birthday, DateCustom deathday) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.picture = picture;
        this.biography = biography;
        this.birthday = birthday;
        this.deathday = deathday;
    }

    // Getters y Setters con estilo encadenado
    public String getId() { return id; }
    public Person setId(String id) { this.id = id; return this; }
    public String getName() { return name; }
    public Person setName(String name) { this.name = name; return this; }
    public String getCountry() { return country; }
    public Person setCountry(String country) { this.country = country; return this; }
    public String getPicture() { return picture; }
    public Person setPicture(String picture) { this.picture = picture; return this; }
    public String getBiography() { return biography; }
    public Person setBiography(String biography) { this.biography = biography; return this; }
    public DateCustom getBirthday() { return birthday; }
    public Person setBirthday(DateCustom birthday) { this.birthday = birthday; return this; }
    public DateCustom getDeathday() { return deathday; }
    public Person setDeathday(DateCustom deathday) { this.deathday = deathday; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                Objects.equals(country, person.country) &&
                Objects.equals(picture, person.picture) &&
                Objects.equals(biography, person.biography) &&
                Objects.equals(birthday, person.birthday) &&
                Objects.equals(deathday, person.deathday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, picture, biography, birthday, deathday);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("country='" + country + "'")
                .add("picture='" + picture + "'")
                .add("biography='" + biography + "'")
                .add("birthday=" + birthday)
                .add("deathday=" + deathday)
                .toString();
    }
}
